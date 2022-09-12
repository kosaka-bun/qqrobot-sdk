package de.honoka.qqrobot.starter.framework.mirai;

import de.honoka.qqrobot.starter.framework.mirai.model.MiraiMessage;
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

    protected final MiraiFramework miraiFramework;

    public MiraiEventListener(MiraiFramework miraiFramework) {
        this.miraiFramework = miraiFramework;
    }

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
        if(!miraiFramework.getMiraiProperties().getSendTestMessageOnRelogin())
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

    //群消息
    @EventHandler
    public void onGroupMessage(GroupMessageEvent e) {
        miraiFramework.getFrameworkCallback().onGroupMsg(
                e.getGroup().getId(),
                e.getSender().getId(),
                miraiFramework.transform(new MiraiMessage(e.getMessage()))
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

    private void onUserMessage(UserMessageEvent e) {
        miraiFramework.getFrameworkCallback().onPrivateMsg(
                e.getSender().getId(),
                miraiFramework.transform(new MiraiMessage(e.getMessage()))
        );
    }
}
