package de.honoka.qqrobot.framework.impl.mirai;

import cn.hutool.core.util.ObjectUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.honoka.qqrobot.framework.BaseFramework;
import de.honoka.qqrobot.framework.api.model.RobotMessage;
import de.honoka.qqrobot.framework.api.model.RobotMessageType;
import de.honoka.qqrobot.framework.api.model.RobotMultipartMessage;
import de.honoka.qqrobot.framework.impl.mirai.component.MiraiEventListener;
import de.honoka.qqrobot.framework.impl.mirai.config.MiraiProperties;
import de.honoka.qqrobot.framework.impl.mirai.model.MiraiMessage;
import de.honoka.qqrobot.starter.RobotBasicProperties;
import de.honoka.sdk.util.file.FileUtils;
import de.honoka.sdk.util.text.TextUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.ExternalResource;
import org.jsoup.Jsoup;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 使用mirai框架中提供的接口实现基本框架
 */
@Slf4j
@Getter
@Component
public class MiraiFramework extends BaseFramework<MiraiMessage> {

    @Resource
    private RobotBasicProperties basicProperties;

    @Resource
    private MiraiProperties miraiProperties;

    /**
     * 提供登录账号，获取信息，发送信息等服务的对象
     */
    private Bot miraiApi;

    /**
     * 事件监听器
     */
    private final List<ListenerHost> listeners = new ArrayList<>();

    private BotInitProperties botInitProperties;

    private boolean started = false;

    @AllArgsConstructor
    private static class BotInitProperties {

        private Long qq;

        private String password;

        private BotConfiguration conf;
    }

    /**
     * 获取配置对象中的信息，构建框架
     */
    @PostConstruct
    public void init() {
        Long qq = basicProperties.getQq();
        String password = basicProperties.getPassword();
        if(!ObjectUtil.isAllNotEmpty(qq, password)) {
            throw new RuntimeException("QQ号或密码不能为空");
        }
        boolean redirectLogs = miraiProperties.getRedirectLogs();
        //修改配置
        BotConfiguration conf = BotConfiguration.getDefault();
        //转移日志存放目录，设置设备信息
        //定义设备信息文件路径，与存放日志的目录路径
        String deviceInfoPath = FileSystems.getDefault().getPath(
            FileUtils.getMainClasspath(),
            miraiProperties.getWorkDirectory(),
            "deviceInfo.json"
        ).toString();
        String logBase = FileSystems.getDefault().getPath(
            FileUtils.getMainClasspath(),
            miraiProperties.getWorkDirectory(),
            "log"
        ) + File.separator;
        log.info("mirai日志已重定向至{}", logBase);
        log.info("若在登录过程中发现任何问题，请前往上述目录中打开日志文件进行排查");
        String cacheDirPath = FileSystems.getDefault().getPath(
            FileUtils.getMainClasspath(),
            miraiProperties.getWorkDirectory(),
            "cache"
        ).toString();
        File botLogDir = new File(logBase + "bot");
        File networkLogDir = new File(logBase + "network");
        File cacheDir = new File(cacheDirPath);
        //检查文件与目录是否存在，不存在则创建
        FileUtils.checkOrMkdirs(botLogDir, networkLogDir);
        //利用这些文件和目录修改配置
        conf.fileBasedDeviceInfo(deviceInfoPath);
        if(redirectLogs) {
            conf.redirectBotLogToDirectory(botLogDir);
            conf.redirectNetworkLogToDirectory(networkLogDir);
        }
        conf.setCacheDir(cacheDir);
        //网络设置
        conf.setProtocol(miraiProperties.getProtocol());
        //构建框架
        botInitProperties = new BotInitProperties(qq, password, conf);
        miraiApi = BotFactory.INSTANCE.newBot(qq, password, conf);
    }

    public void addListener(ListenerHost listener) {
        listeners.add(listener);
    }

    //以下是使用mirai平台对标准框架方法的实现

    /**
     * 启动框架，登录账号，对接消息处理的回调方法（延迟注入事件监听器）
     */
    @Override
    public synchronized void boot() {
        miraiApi.login();
        //默认监听器
        addListener(new MiraiEventListener(this));
        //设置监听
        for(ListenerHost listener : listeners) {
            miraiApi.getEventChannel().registerListenerHost(listener);
        }
        frameworkCallback.onStartup();
        started = true;
    }

    @Override
    public synchronized void stop() {
        miraiApi.close(null);
        frameworkCallback.onShutdown();
    }

    @Override
    public synchronized void reboot() {
        try {
            miraiApi.login();
        } catch(Throwable t) {
            log.warn("\nMirai Bot直接重连失败，异常堆栈信息如下：", t);
            log.info("\n重新构造Mirai Bot");
            miraiApi = BotFactory.INSTANCE.newBot(
                botInitProperties.qq,
                botInitProperties.password,
                botInitProperties.conf
            );
            miraiApi.login();
            for(ListenerHost listener : listeners) {
                miraiApi.getEventChannel().registerListenerHost(listener);
            }
            log.info("\n登录成功");
        }
    }

    @Scheduled(cron = "0 * * * * ?")
    public synchronized void checkIsActive() {
        if(!started || miraiApi.isOnline()) return;
        reboot();
    }

    @SneakyThrows
    @Override
    public MiraiMessage transform(Long group, long qq, RobotMultipartMessage message) {
        if(message == null || message.isEmpty()) return null;
        MessageChainBuilder builder = new MessageChainBuilder();
        List<ExternalResource> externalResources = new ArrayList<>();
        for(RobotMessage<?> part : message.messageList) {
            switch(part.getType()) {
                case TEXT:
                    if(part.getContent() == null || part.getContent().equals("")) continue;
                    builder.add((String) part.getContent());
                    break;
                case AT:
                    if(group == null) break;
                    builder.add(new At((long) part.getContent()));
                    break;
                case IMAGE:
                    InputStream imageBytes = (InputStream) part.getContent();
                    ExternalResource imgRes = ExternalResource.create(imageBytes);
                    externalResources.add(imgRes);
                    //判断是否是私聊消息，以判断通过何种途径上传文件
                    Image img;
                    if(group == null) {
                        img = Objects.requireNonNull(getPrivateContact(qq)).uploadImage(imgRes);
                    } else {
                        img = Objects.requireNonNull(miraiApi.getGroup(group)).uploadImage(imgRes);
                    }
                    builder.add(img);
                    break;
                case FILE:
                    InputStream fileBytes = (InputStream) part.getContent();
                    //若群对象不存在，不予发送
                    Group groupObj = miraiApi.getGroup(Objects.requireNonNull(group));
                    if(groupObj == null) break;
                    //机器人在该群被禁言，不予发送
                    if(isMuted(group)) break;
                    //发送消息
                    String fileName = (String) part.getOthers().get("fileName");
                    try(ExternalResource res = ExternalResource.create(fileBytes)) {
                        groupObj.getFiles().uploadNewFile(fileName, res);
                    }
                    break;
            }
        }
        return new MiraiMessage(builder.build()).setExternalResources(externalResources);
    }

    @Override
    public RobotMultipartMessage transform(MiraiMessage message) {
        MessageChain miraiMultiPartMsg = message.getMessageChain();
        RobotMultipartMessage multipartMessage = new RobotMultipartMessage();
        for(SingleMessage sm : miraiMultiPartMsg) {
            if(sm.getClass().equals(At.class)) {
                multipartMessage.add(RobotMessageType.AT, ((At) sm).getTarget());
            } else {
                multipartMessage.add(RobotMessageType.TEXT, sm.contentToString());
            }
        }
        return multipartMessage;
    }

    @Override
    public void sendPrivateMsg(long qq, RobotMultipartMessage message) {
        //查找此用户
        Contact contact = getPrivateContact(qq);
        //若不存在，不予发送
        if(contact == null) return;
        //发送消息
        MiraiMessage msgAndRes = transform(null, qq, message);
        sendMessage(contact, msgAndRes);
    }

    @Override
    public void sendGroupMsg(long group, RobotMultipartMessage message) {
        //若群对象不存在，不予发送
        Group groupObj = miraiApi.getGroup(group);
        if(groupObj == null) return;
        //机器人在该群被禁言，不予发送
        if(isMuted(group)) return;
        //发送消息
        MiraiMessage msgAndRes = transform(group, 0, message);
        sendMessage(groupObj, msgAndRes);
    }

    private void sendMessage(Contact contact, MiraiMessage msgAndRes) {
        if(msgAndRes == null) return;
        MessageChain msg = msgAndRes.getMessageChain();
        List<ExternalResource> externalResources = msgAndRes.getExternalResources();
        DateFormat dateFormat = TextUtils.getSimpleDateFormat();
        //当消息未成功发送时多次尝试
        for(int tryTimes = 0; tryTimes < 3; tryTimes++) {
            //尝试发送
            try {
                contact.sendMessage(msg);
            } catch(Exception e) {
                //未发送成功，重试
                //报告错误
                System.err.println(dateFormat.format(new Date()));
                System.err.println("消息发送失败！已尝试次数：" + (tryTimes + 1));
                System.err.println("要发送的内容：\n" + msg.contentToString());
                e.printStackTrace();
                //是否需要重发
                if(!basicProperties.getResendOnSendFailed()) break;
                continue;
            }
            //发送成功，不再继续尝试
            //若非第一次尝试发送
            if(tryTimes > 0) {
                System.out.println(dateFormat.format(new Date()));
                System.out.println("消息重发成功：\n" + msg.contentToString());
            }
            break;
        }
        //关闭资源
        for(ExternalResource res : externalResources) {
            try {
                res.close();
            } catch(Throwable t) {
                //ignore
            }
        }
    }

    /**
     * 获取一个私聊联系方式，若用户在好友列表中，则返回好友对象，在群内则
     * 返回群成员，均不在返回null
     */
    private Contact getPrivateContact(long qq) {
        //查找此用户
        //查看是否在好友列表中
        Contact contact = miraiApi.getFriends().get(qq);
        if(contact != null) return contact;
        //若不在，遍历机器人所加的每个群，查找可用的群成员
        for(Group group : miraiApi.getGroups()) {
            //群中不存在此用户，查看下一个群
            if(!group.contains(qq)) continue;
            //存在则返回
            return group.get(qq);
        }
        //所有的群都不存在，则返回null
        return null;
    }

    @Override
    public String getGroupName(long group) {
        try {
            return Objects.requireNonNull(miraiApi.getGroup(group)).getName();
        } catch(Exception e) {
            return "未知";
        }
    }

    @Override
    public synchronized String getNickOrCard(long group, long qq) {
        try {
            String nameCard = Objects.requireNonNull(Objects.requireNonNull(
                    miraiApi.getGroup(group)).get(qq)).getNameCard();
            //群名片为空串，代表没有设置群名片
            if(nameCard.trim().isEmpty()) {
                nameCard = Objects.requireNonNull(
                    Objects.requireNonNull(miraiApi.getGroup(group)).get(qq)
                ).getNick();
            }
            return nameCard;
        } catch(Exception e) {
            return getStrangerNick(qq);
        }
    }

    /**
     * 调用第三方API，获取非好友的的昵称（可能需要随时更新此方法）
     */
    private static String getStrangerNick(long qq) {
        try {
            //定义接口URL
            String url = "https://r.qzone.qq.com/fcg-bin/cgi_get_portrait.fcg?uins=%d";
            //获取响应文本（不得请求时间过长，否则会卡住很多操作）
            byte[] response = Jsoup.connect(String.format(url, qq)).timeout(5 * 1000)
                .execute().bodyAsBytes();
            String jsonStr = new String(response, "GB18030");
            //处理响应文本
            jsonStr = jsonStr.substring(jsonStr.indexOf("{"), jsonStr.lastIndexOf("}") + 1);
            //提取为Json对象
            JsonObject json = JsonParser.parseString(jsonStr).getAsJsonObject();
            //从Json对象中提取某个属性的值
            JsonArray ja = json.get(String.valueOf(qq)).getAsJsonArray();
            return ja.get(6).getAsString();
        } catch(Exception e) {
            return String.valueOf(qq);
        }
    }

    @Override
    public boolean isMuted(long group) {
        try {
            return Objects.requireNonNull(miraiApi.getGroup(group)).getBotMuteRemaining() > 0;
        } catch(Exception e) {
            //机器人不在群中，无法发言，默认被禁言
            return true;
        }
    }

    /**
     * 发送测试消息
     */
    public boolean sendTestMessageOnRelogin() {
        //未配置开发群，默认测试消息发送成功
        if(basicProperties.getDevelopingGroup() == null) return true;
        //发送测试消息
        MessageReceipt<?> receipt = null;
        try {
            receipt = Objects.requireNonNull(
                miraiApi.getGroup(basicProperties.getDevelopingGroup())
            ).sendMessage(
                new SimpleDateFormat("HH:mm:ss").format(new Date()) + "\n平台重新登录"
            );
        } catch(Throwable ex) {
            //none
        }
        return receipt != null;
    }
}
