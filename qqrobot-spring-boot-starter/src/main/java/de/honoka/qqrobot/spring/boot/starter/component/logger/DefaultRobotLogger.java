package de.honoka.qqrobot.spring.boot.starter.component.logger;

public class DefaultRobotLogger implements RobotLogger {

    @Override
    public void logException(Throwable t) {}

    @Override
    public void logMsgExecution(Long group, long qq, String msg, String reply) {}
}
