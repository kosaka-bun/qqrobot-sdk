package de.honoka.qqrobot.starter.common;

@SuppressWarnings("unused")
public interface ConstantMessage {

    String ERROR = "出现了异常，已将信息报告给开发者";

    String WRONG_PARAMETER = "指定的参数有误";

    String PARAMETER_NOT_ENOUGH = "命令格式有误，提供的参数个数不足";

    String NO_AUTHORIZATION = "你没有足够的权限执行此操作";

    String OFF = "处理消息功能处于关闭状态，可能在维护当中";
}
