package de.honoka.qqrobot.starter.framework.impl.tester;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import de.honoka.qqrobot.framework.api.message.RobotMessage;
import de.honoka.qqrobot.framework.api.message.RobotMessageTypes;
import de.honoka.qqrobot.framework.api.message.RobotMultipartMessage;
import de.honoka.qqrobot.starter.common.NoContactException;
import de.honoka.qqrobot.starter.common.RobotMutedException;
import de.honoka.qqrobot.starter.framework.TypedRobotFramework;
import de.honoka.qqrobot.starter.framework.config.TesterConfig;
import de.honoka.qqrobot.starter.framework.config.TesterProperties;
import de.honoka.qqrobot.starter.framework.impl.tester.model.TesterMessage;
import de.honoka.qqrobot.starter.framework.impl.tester.model.TesterMessageTypes;
import de.honoka.qqrobot.starter.framework.impl.tester.model.TesterRobotMessage;
import de.honoka.qqrobot.starter.framework.impl.tester.server.TesterServer;
import de.honoka.qqrobot.starter.framework.impl.tester.server.TesterServerConnection;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Getter
@Component
public class TesterFramework extends TypedRobotFramework<TesterRobotMessage> {

    @Resource
    private TesterProperties testerProperties;

    @Resource
    private TesterConfig testerConfig;

    @Resource
    private TesterServer testerServer;

    //key为图片数据流的hashCode，value为这个流对应的文件名
    private final Map<Integer, String> imageNameMap = new HashMap<>();

    @SneakyThrows
    @Override
    public synchronized void boot() {
        File imagePath = new File(testerProperties.getImagePath());
        if(imagePath.exists()) {
            FileUtil.del(imagePath);
        }
        frameworkCallback.onStartup();
        log.info("\nTester框架启动完成\n请访问 {} 进行测试", testerConfig.getTesterUrl());
    }

    @SneakyThrows
    @Override
    public synchronized void stop() {
        for(TesterServerConnection connection : testerServer.getConnections()) {
            connection.getSession().close();
        }
        testerServer.resetConnections();
        frameworkCallback.onShutdown();
    }

    @Override
    public void reboot() {
        stop();
        boot();
    }

    @SneakyThrows
    @Override
    public TesterRobotMessage typedTransform(Long group, long qq, RobotMultipartMessage message) {
        if(message == null || message.isEmpty()) return null;
        TesterRobotMessage testerRobotMessage = new TesterRobotMessage();
        for(RobotMessage<?> part : message.messageList) {
            Object content = part.getContent();
            if(content instanceof String) {
                if(content.equals("")) continue;
                testerRobotMessage.add(TesterRobotMessage.PartType.TEXT, (String) content);
            }
            if(content instanceof RobotMessageTypes.At) {
                if(group == null) break;
                long atQq = ((RobotMessageTypes.At) content).getQq();
                TesterRobotMessage.Part at = new TesterRobotMessage.Part(
                    TesterRobotMessage.PartType.AT, "@" + getNickOrCard(group, atQq) + " "
                );
                at.setExtras(new JSONObject());
                at.getExtras().set("qq", atQq);
                testerRobotMessage.add(at);
            }
            if(content instanceof RobotMessageTypes.Image) {
                InputStream inputStream = ((RobotMessageTypes.Image) content).getContent();
                String name = imageNameMap.get(inputStream.hashCode());
                if(name == null) {
                    name = UUID.randomUUID().toString();
                    imageNameMap.put(inputStream.hashCode(), name);
                    byte[] bytes = IoUtil.readBytes(inputStream, false);
                    String path = Paths.get(testerProperties.getImagePath(), name + ".png").toString();
                    FileUtil.touch(new File(path));
                    try(OutputStream os = Files.newOutputStream(Paths.get(path))) {
                        os.write(bytes);
                    }
                }
                testerRobotMessage.add(TesterRobotMessage.PartType.IMAGE, name);
            }
            if(content instanceof RobotMessageTypes.File) {
                testerRobotMessage.add(TesterRobotMessage.PartType.TEXT, "【文件】");
            }
        }
        return testerRobotMessage;
    }

    @Override
    public RobotMultipartMessage typedTransform(TesterRobotMessage message) {
        RobotMultipartMessage multipartMessage = new RobotMultipartMessage();
        for(TesterRobotMessage.Part part : message.getParts()) {
            if(part.getType().equals(TesterRobotMessage.PartType.AT)) {
                multipartMessage.add(RobotMessage.at(part.getExtras().getLong("qq")));
            } else {
                multipartMessage.add(part.getContent());
            }
        }
        return multipartMessage;
    }

    @Override
    public void sendPrivateMsg(long qq, RobotMultipartMessage message) {
        boolean found = false;
        for(TesterServerConnection connection : testerServer.getConnections()) {
            long qqOfConnection = connection.getData().getLong("qq");
            if(qqOfConnection != qq) continue;
            found = true;
            JSONObject data = new JSONObject();
            data.set("name", "Robot");
            data.set("content", typedTransform(null, qq, message).toJsonArray());
            connection.sendMessage(
                new TesterMessage(null)
                    .setType(TesterMessageTypes.PRIVATE_MESSAGE)
                    .setData(data)
            );
        }
        if(found) return;
        throw new NoContactException(qq, null);
    }
    
    @Override
    public void sendGroupMsg(long group, RobotMultipartMessage message) {
        if(testerServer.getConnections().isEmpty()) {
            throw new RobotMutedException(group);
        }
        for(TesterServerConnection connection : testerServer.getConnections()) {
            JSONObject data = new JSONObject();
            data.set("name", "Robot");
            data.set("content", typedTransform(group, 0, message).toJsonArray());
            connection.sendMessage(
                new TesterMessage(null)
                    .setType(TesterMessageTypes.GROUP_MESSAGE)
                    .setData(data)
            );
        }
    }

    @Override
    public String getGroupName(long group) {
        return "Tester Group";
    }

    @Override
    public String getNickOrCard(long group, long qq) {
        for(TesterServerConnection connection : testerServer.getConnections()) {
            long qqOfConnection = connection.getData().getLong("qq");
            if(qqOfConnection == qq) {
                return connection.getData().getStr("name");
            }
        }
        return null;
    }

    @Override
    public boolean isMuted(long group) {
        return false;
    }
}
