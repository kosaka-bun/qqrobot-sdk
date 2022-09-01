package de.honoka.qqrobot.spring.boot.starter.component.logger;

/**
 * 由用户自行实现的日志记录器
 */
public interface RobotLogger {

    /**
     * 将异常信息存储到数据库中
     */
    void logException(Throwable t);

    /**
     * 记录消息处理的相关信息
     */
    void logMsgExecution(Long group, long qq, String msg, String reply);
}
