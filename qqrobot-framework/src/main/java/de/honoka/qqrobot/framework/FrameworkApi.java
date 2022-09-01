package de.honoka.qqrobot.framework;

import java.io.InputStream;

/**
 * 机器人服务框架必须提供的方法
 */
public interface FrameworkApi {
	
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
	 * 使带有CQ码的字符串类型转换为平台支持的多部分消息
	 */
	Object transform(Long group, long qq, String str);
	
	/**
	 * 使平台支持的多部分消息转换为带CQ码（如果有）的字符串消息
	 */
	String transform(Object multiPartMsg);
	
	/**
	 * 向指定QQ发送一条私聊消息
	 */
	void sendPrivateMsg(long qq, String msg);
	
	/**
	 * 向指定群发送一条群消息
	 */
	void sendGroupMsg(Long group, String msg);

	void sendFileToGroup(Long group, String fileName, InputStream inputStream);
	
	/**
	 * 向用户回复一条消息，根据群号与QQ号判断采用私聊回复还是群聊回复（加上at）
	 */
	void reply(Long group, long qq, String msg);
	
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
