package de.honoka.qqrobot.spring.boot.starter.framework.mirai;

import de.honoka.sdk.util.code.CodeUtils;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.*;

/**
 * mirai框架的事件监听器，专用于对接机器人主类的消息处理方法
 */
@SuppressWarnings("unused")
@Slf4j
public class MiraiEventListener extends SimpleListenerHost {

    //一个账号请求添加机器人为好友的事件
    @EventHandler
    public void onNewFriendRequest(NewFriendRequestEvent e) {
        //只接受来自于群的好友添加
        if(e.getFromGroup() != null) e.accept();
        else e.reject(false);	//是否拒绝后拉黑
    }

    //平台主动或被动重新登录. 在此事件广播前就已经登录完毕
    @EventHandler
    public void onRelogin(BotReloginEvent e) {
        log.info("平台重新登录");
        if(!miraiFramework.miraiProperties.getSendTestMessageOnRelogin())
            return;
        //发送测试消息
        boolean success = miraiFramework.sendTestMessageOnRelogin();
        //发送成功，返回
        if(success) return;
        //未发送成功，重新登录
        log.error("测试消息发送失败，准备再次重新登录");
        CodeUtils.threadSleep(3000);
        miraiFramework.miraiApi.login();
    }

    //服务器主动要求更换另一个服务器
    @EventHandler
    public void onRequireReconnect(BotOfflineEvent.RequireReconnect e) {
        log.warn("服务器主动要求更换另一个服务器");
    }

    //平台因 returnCode = -10008 等原因掉线
    //@EventHandler
    //public void onCode10008(BotOfflineEvent.PacketFactoryErrorCode e) {
    //	log.warn("平台因 returnCode = -10008 等原因掉线", e.getCause());
    //}

    //平台因网络问题而掉线
    @EventHandler
    public void onDropped(BotOfflineEvent.Dropped e) {
        log.warn("平台因网络问题而掉线", e.getCause());
    }

    //平台被服务器断开连接
    @EventHandler
    public void onDisconnected(BotOfflineEvent.MsfOffline e) {
        log.warn("平台被服务器断开连接", e.getCause());
    }

    //消息发送后
    //@EventHandler
    //public void onMsgSend(MessagePostSendEvent<?> e) {
    //
    //}

    //群消息
    @EventHandler
    public void onGroupMessage(GroupMessageEvent e) {
        miraiFramework.frameworkCallback.onGroupMsg(
                e.getGroup().getId(),
                e.getSender().getId(),
                miraiFramework.transform(new Object[] { e.getMessage() })
        );
    }

    private void onUserMessage(UserMessageEvent e) {
        miraiFramework.frameworkCallback.onPrivateMsg(
                e.getSender().getId(),
                miraiFramework.transform(new Object[] { e.getMessage() })
        );
    }

    //临时会话
    @EventHandler
    public void onTempMessage(GroupTempMessageEvent e) {
        onUserMessage(e);
    }

    //好友会话
    @EventHandler
    public void onFriendMessage(FriendMessageEvent e) {
        onUserMessage(e);
    }

    public MiraiEventListener(MiraiFramework miraiFramework) {
        this.miraiFramework = miraiFramework;
    }

    protected final MiraiFramework miraiFramework;
}
