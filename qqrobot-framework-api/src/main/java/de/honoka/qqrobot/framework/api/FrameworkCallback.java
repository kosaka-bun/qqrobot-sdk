package de.honoka.qqrobot.framework.api;

import de.honoka.qqrobot.framework.api.model.RobotMultipartMessage;

/**
 * 框架回调
 */
public interface FrameworkCallback {
	
	/**
	 * 收到私聊消息
	 */
	void onPrivateMsg(long qq, RobotMultipartMessage msg);
	
	/**
	 * 收到群消息
	 */
	void onGroupMsg(Long group, long qq, RobotMultipartMessage msg);
	
	/**
	 * 框架启动时
	 */
	void onStartup();
	
	/**
	 * 框架关闭时
	 */
	void onShutdown();
}
