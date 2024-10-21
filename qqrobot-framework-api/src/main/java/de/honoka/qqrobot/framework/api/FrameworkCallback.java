package de.honoka.qqrobot.framework.api;

import de.honoka.qqrobot.framework.api.model.CallerInfo;
import de.honoka.qqrobot.framework.api.model.RobotMultipartMessage;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 框架回调
 */
public abstract class FrameworkCallback {
	
	/*
	 * RobotController中可能会有会话，将导致消息处理方法长时间不返回，若有大量会话占用了线程，
	 * 则不应将后续的处理消息的任务放入阻塞队列中，否则将导致机器人在很长时间后才响应用户在很久
	 * 之前调用的命令。
	 * SynchronousQueue相当于一个长度为0的队列，不保存任何任务，总是处于充满的状态。
	 * 因此，当所有核心线程全部被占用时，不会将任务放入阻塞队列，而是立即启动或复用非核心线程来
	 * 执行任务，直到总线程数达到最大线程数后，采用拒绝策略。
	 */
	private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
		5, 20, 60, TimeUnit.SECONDS,
		new SynchronousQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy()
	);
	
	/**
	 * 用于在全局范围内获取命令调用者的信息
	 */
	public static final ThreadLocal<CallerInfo> callerInfoHolder = new ThreadLocal<>();
	
	/**
	 * 收到私聊消息（好友私聊或群临时会话）
	 */
	protected abstract void onPrivateMsg(RobotMultipartMessage msg);
	
	/**
	 * 收到群消息
	 */
	protected abstract void onGroupMsg(RobotMultipartMessage msg);
	
	/**
	 * 框架启动时
	 */
	public abstract void onStartup();
	
	/**
	 * 框架关闭时
	 */
	public abstract void onShutdown();
	
	public final void onPrivateMsg(Long group, long qq, RobotMultipartMessage msg) {
		executor.submit(() -> {
			callerInfoHolder.set(new CallerInfo().setGroup(group).setQq(qq).setGroupMsg(false));
			onPrivateMsg(msg);
			callerInfoHolder.remove();
		});
	}
	
	public final void onGroupMsg(long group, long qq, RobotMultipartMessage msg) {
		executor.submit(() -> {
			callerInfoHolder.set(new CallerInfo().setGroup(group).setQq(qq).setGroupMsg(true));
			onGroupMsg(msg);
			callerInfoHolder.remove();
		});
	}
}
