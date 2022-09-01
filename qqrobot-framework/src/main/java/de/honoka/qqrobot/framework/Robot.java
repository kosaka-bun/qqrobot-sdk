package de.honoka.qqrobot.framework;

/**
 * 机器人主类必须提供的方法
 */
public interface Robot {
	
	/**
	 * 收到私聊消息
	 */
	void onPrivateMsg(long qq, String msg);
	
	/**
	 * 收到群消息
	 */
	void onGroupMsg(Long group, long qq, String msg);
	
	/**
	 * 框架启动时
	 */
	void onStartup();
	
	/**
	 * 框架关闭时
	 */
	void onShutdown();
}
