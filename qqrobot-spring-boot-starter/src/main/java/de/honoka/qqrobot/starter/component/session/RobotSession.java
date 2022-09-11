package de.honoka.qqrobot.starter.component.session;

import de.honoka.qqrobot.framework.model.RobotMultipartMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

/**
 * 会话类，用于记录处于会话状态的qq号和群号，以及这些号码在进入会话状态以后发送的信息
 */
@Getter
@Setter
public class RobotSession implements AutoCloseable {

    private long qq;

    private Long group;

    private volatile RobotMultipartMessage reply;

    /**
     * 所属的会话管理器
     */
    private final SessionManager sessionManager;

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
    public RobotMultipartMessage waitingForReply(int timeout) throws TimeoutException {
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

    public static class TimeoutException extends Exception {}

    @Override
    public void close() {
        sessionManager.closeSession(this);
    }
}
