package de.honoka.qqrobot.starter.common;

import de.honoka.qqrobot.framework.api.FrameworkCallback;
import de.honoka.qqrobot.starter.component.DefaultFrameworkCallback;
import de.honoka.qqrobot.starter.component.logger.DefaultRobotLogger;
import de.honoka.qqrobot.starter.component.logger.RobotLogger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConditionalBeans {

    @ConditionalOnMissingBean(RobotLogger.class)
    @Bean
    public DefaultRobotLogger defaultRobotLogger() {
        return new DefaultRobotLogger();
    }

    @ConditionalOnMissingBean(FrameworkCallback.class)
    @Bean
    public DefaultFrameworkCallback defaultFrameworkCallback() {
        return new DefaultFrameworkCallback();
    }
}
