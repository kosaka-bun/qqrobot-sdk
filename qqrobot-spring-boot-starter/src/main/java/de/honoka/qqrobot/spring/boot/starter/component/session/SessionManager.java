package de.honoka.qqrobot.spring.boot.starter.component.session;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Vector;

/**
 * 系统会话列表的管理器
 */
@Component
public class SessionManager {
	
	/**
	 * 当前会话列表
	 */
	private final List<RobotSession> sessionList = new Vector<>();
	
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
	 * 开启一个会话
	 */
	public RobotSession openSession(Long group, long qq) {
		//检查是否有存在的会话，如果有，返回该会话
		RobotSession session = getCurrentSession(group, qq);
		if(session != null) return session;
		session = new RobotSession(group, qq, this);
		sessionList.add(session);
		return session;
	}
	
	/**
	 * 关闭一个会话
	 */
	public void closeSession(RobotSession s) {
		sessionList.remove(s);
	}
	
	public void closeSession(Long group, long qq) {
		RobotSession s = getCurrentSession(group, qq);
		if(s != null) sessionList.remove(s);
	}
}
