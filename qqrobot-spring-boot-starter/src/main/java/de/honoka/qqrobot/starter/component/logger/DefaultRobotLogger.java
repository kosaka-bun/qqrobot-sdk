package de.honoka.qqrobot.starter.component.logger;

import de.honoka.qqrobot.framework.model.RobotMultipartMessage;
import de.honoka.qqrobot.starter.common.ConditionalBeans;
import de.honoka.qqrobot.starter.common.annotation.ConditionalComponent;

@ConditionalComponent(ConditionalBeans.class)
public class DefaultRobotLogger implements RobotLogger {

    @Override
    public void logException(Throwable t) {}

    @Override
    public void logMsgExecution(Long group, long qq,
                                RobotMultipartMessage msg,
                                RobotMultipartMessage reply) {}
}
