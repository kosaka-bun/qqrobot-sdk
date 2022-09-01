package de.honoka.qqrobot.framework;

import de.honoka.qqrobot.framework.model.RobotMessageType;
import de.honoka.qqrobot.framework.model.RobotMultipartMessage;

/**
 * 机器人服务框架必须提供的方法
 * @param <M> 表示平台的消息格式
 */
public interface FrameworkApi<M> {

    /**
     * 启动框架
     */
    void boot();

    /**
     * 停止运行框架
     */
    void stop();

    /**
     * 重新启动框架
     */
    void reboot();

    /**
     * 将统一消息格式转换为平台消息格式
     */
    M transform(Long group, long qq, RobotMultipartMessage message);

    /**
     * 使平台支持的多部分消息转换为统一消息格式
     */
    RobotMultipartMessage transform(M message);

    /**
     * 向指定QQ发送一条私聊消息
     */
    void sendPrivateMsg(long qq, RobotMultipartMessage message);

    default void sendPrivateMsg(long qq, String text) {
        sendPrivateMsg(qq, RobotMultipartMessage.of(RobotMessageType.TEXT, text));
    }

    /**
     * 向指定群发送一条群消息
     */
    void sendGroupMsg(Long group, RobotMultipartMessage message);

    default void sendGroupMsg(Long group, String text) {
        sendGroupMsg(group, RobotMultipartMessage.of(RobotMessageType.TEXT, text));
    }

    /**
     * 向用户回复一条消息，根据群号与QQ号判断采用私聊回复还是群聊回复（加上at）
     */
    void reply(Long group, long qq, RobotMultipartMessage message);

    default void reply(Long group, long qq, String text) {
        reply(group, qq, RobotMultipartMessage.of(RobotMessageType.TEXT, text));
    }

    /**
     * 获取指定群的群名
     */
    String getGroupName(Long group);

    /**
     * 获取指定QQ的群名片，若QQ不在群内，则获取QQ的昵称
     */
    String getNickOrCard(Long group, long qq);

    /**
     * 判断机器人在某个群中是否被禁言
     */
    boolean isMuted(Long group);
}
