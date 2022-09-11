package de.honoka.qqrobot.starter.component.logger;

import de.honoka.qqrobot.framework.model.RobotMultipartMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@ConditionalOnMissingBean(RobotLogger.class)
@Component
public class DefaultRobotLogger implements RobotLogger {

    @Override
    public void logException(Throwable t) {}

    @Override
    public void logMsgExecution(Long group, long qq,
                                RobotMultipartMessage msg,
                                RobotMultipartMessage reply) {}
}
