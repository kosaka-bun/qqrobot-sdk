package de.honoka.qqrobot.starter.component.session;

import de.honoka.qqrobot.framework.api.model.RobotMultipartMessage;
import de.honoka.sdk.util.code.ThrowsConsumer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.concurrent.TimeUnit;

/**
 * 会话类，用于记录处于会话状态的qq号和群号，以及这些号码在进入会话状态以后发送的信息
 */
@Getter
@Setter
@Accessors(chain = true)
public class RobotSession {

    private Long group;

    private long qq;

    private volatile RobotMultipartMessage reply;

    /**
     * 会话过程中要执行的操作
     */
    private ThrowsConsumer<RobotSession> action;

    /**
     * 超时操作
     */
    private ThrowsConsumer<RobotSession> onTimeout;

    /**
     * 所属的会话管理器
     */
    private final SessionManager sessionManager;

    public static class TimeoutException extends Exception {}

    //package-private
    RobotSession(Long group, long qq, SessionManager sessionManager) {
        this.qq = qq;
        this.group = group;
        this.sessionManager = sessionManager;
    }

    /**
     * 等待某个会话的回复，超时抛出超时异常
     * @param timeout 超时时间，单位为秒
     * @return  回复
     */
    public RobotMultipartMessage waitingForReply(int timeout)
            throws TimeoutException {
        reply = null;  //等待回复前，先忽略已有的回复
        int i = 0;  //已等待秒数
        while(reply == null) {
            try {
                TimeUnit.SECONDS.sleep(1);
                i++;
                if(i >= timeout) throw new TimeoutException();
            } catch(Exception e) {
                throw new TimeoutException();
            }
        }
        return reply;
    }

    public void reply(RobotMultipartMessage message) {
        sessionManager.getFramework().reply(group, qq, message);
    }

    public void reply(String message) {
        sessionManager.getFramework().reply(group, qq, message);
    }

    //package-private
    public void run() {
        sessionManager.openSession(this);
        try {
            action.accept(this);
        } catch(Throwable t) {
            //noinspection ConstantConditions
            if(!(t instanceof TimeoutException)) throw t;
            if(onTimeout != null) {
                onTimeout.accept(this);
            } else {
                sessionManager.getFramework().reply(group, qq,
                        "会话已超时关闭");
            }
        } finally {
            close();
        }
    }

    public void close() {
        sessionManager.closeSession(this);
    }
}
