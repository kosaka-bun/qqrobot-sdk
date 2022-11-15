package de.honoka.qqrobot.framework;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 带有配置信息的机器人服务框架基本结构
 */
public abstract class Framework<M> implements FrameworkApi<M> {

	/**
	 * 用于在收到消息时进行对接指定方法进行处理
	 */
	@Getter(AccessLevel.PUBLIC)
	@Setter(AccessLevel.PROTECTED)
	protected FrameworkCallback frameworkCallback;
}
