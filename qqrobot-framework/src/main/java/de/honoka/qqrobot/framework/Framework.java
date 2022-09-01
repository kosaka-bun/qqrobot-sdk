package de.honoka.qqrobot.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 带有配置信息的机器人服务框架基本结构
 */
public abstract class Framework implements FrameworkApi {
	
	/**
	 * 机器人主类，用于在收到消息时进行对接指定方法进行处理
	 */
	public Robot robot;
	
	/**
	 * 用于存储框架的基本配置信息
	 */
	public final Properties frameworkProps;

	public Framework(Robot robot) {
		this.robot = robot;
		frameworkProps = null;
	}
	
	public Framework(Robot robot, Properties frameworkProps) {
		this.robot = robot;
		this.frameworkProps = frameworkProps;
	}
	
	/**
	 * 将消息文本中的文本与CQ码分离，拆分为不同部分添加到列表中
	 */
	protected List<String> splitMessage(String msg) {
		List<String> parts = new ArrayList<>();
		//处理所有的CQ码
		while (msg.contains("[CQ:")) {
			//消息中第一个CQ码的起始位置
			int cqCodeStart = msg.indexOf("[CQ:");
			//消息中第一个CQ码的结束位置（最后一个字符的后一位）
			int cqCodeEnd = msg.indexOf("]", cqCodeStart) + 1;
			//提取第一个CQ码起始位置之前的文本
			parts.add(msg.substring(0, cqCodeStart));
			//提取CQ码文本
			parts.add(msg.substring(cqCodeStart, cqCodeEnd));
			//剪切消息文本，将已提取的消息部分剪去，然后进行下一次判断与剪切
			msg = msg.substring(cqCodeEnd);
		}
		//所有的CQ码提取完成后，消息尚有剩余部分纯文本，将其添加到列表中
		parts.add(msg);
		//优化消息列表，移除无效的部分
		for(int i = parts.size() - 1; i >= 0; i--) {
			if(parts.get(i) == null || parts.get(i).equals(""))
				parts.remove(i);
		}
		return parts;
	}
}
