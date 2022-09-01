package de.honoka.qqrobot.spring.boot.starter.component.logger;

import de.honoka.qqrobot.framework.model.RobotMultipartMessage;

public class DefaultRobotLogger implements RobotLogger {

    @Override
    public void logException(Throwable t) {}

    @Override
    public void logMsgExecution(Long group, long qq,
                                RobotMultipartMessage msg,
                                RobotMultipartMessage reply) {}
}
