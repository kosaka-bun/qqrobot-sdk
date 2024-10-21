package de.honoka.qqrobot.starter.component.session;

import de.honoka.qqrobot.framework.api.Framework;
import de.honoka.sdk.util.code.ThrowsConsumer;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 系统会话列表的管理器
 */
@Component
public class SessionManager {

    @Getter(AccessLevel.PACKAGE)
    @Lazy
    @Resource
    private Framework<?> framework;

    /**
     * 当前会话列表
     */
    private final List<RobotSession> sessionList = Collections.synchronizedList(
            new LinkedList<>());

    /**
     * 获取一个会话
     */
    public RobotSession getCurrentSession(Long group, long qq) {
        //任何对列表的遍历之前，都必须对列表加锁，阻止其他线程对列表的修改
        synchronized (sessionList) {
            for(RobotSession s : sessionList) {
                if(qq == s.getQq() && Objects.equals(group, s.getGroup()))
                    return s;
            }
            return null;
        }
    }

    /**
     * 创建一个会话
     */
    public RobotSession createSession(Long group, long qq) {
        //检查是否有存在的会话
        RobotSession session = getCurrentSession(group, qq);
        if(session != null) return session;
        return new RobotSession(group, qq, this);
    }

    //package-private
    /**
     * 启动一个会话
     */
    void openSession(RobotSession session) {
        //检查是否有存在的会话
        RobotSession existSession = getCurrentSession(session.getGroup(),
                session.getQq());
        if(existSession != null) return;
        //添加
        sessionList.add(session);
    }

    public void openSession(Long group, long qq, ThrowsConsumer<RobotSession> action,
                            ThrowsConsumer<RobotSession> onTimeout) {
        createSession(group, qq).setAction(action).setOnTimeout(onTimeout).run();
    }

    /**
     * 关闭一个会话
     */
    public void closeSession(Long group, long qq) {
        RobotSession s = getCurrentSession(group, qq);
        if(s != null) sessionList.remove(s);
    }

    //package-private
    void closeSession(RobotSession s) {
        sessionList.remove(s);
    }
}
