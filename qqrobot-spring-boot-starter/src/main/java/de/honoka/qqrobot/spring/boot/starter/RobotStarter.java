package de.honoka.qqrobot.spring.boot.starter;

import de.honoka.qqrobot.framework.FrameworkCallback;
import de.honoka.qqrobot.spring.boot.starter.component.DefaultFrameworkCallback;
import de.honoka.qqrobot.spring.boot.starter.component.MessageExecutor;
import de.honoka.qqrobot.spring.boot.starter.component.RobotBeanHolder;
import de.honoka.qqrobot.spring.boot.starter.component.logger.DefaultRobotLogger;
import de.honoka.qqrobot.spring.boot.starter.component.logger.RobotLogger;
import de.honoka.qqrobot.spring.boot.starter.property.RobotBasicProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@ComponentScan("de.honoka.qqrobot.spring.boot.starter.component")
@EnableConfigurationProperties(RobotBasicProperties.class)
@Configuration
public class RobotStarter {

    @ConditionalOnMissingBean(FrameworkCallback.class)
    @Bean
    public FrameworkCallback robot(MessageExecutor messageExecutor,
                                   RobotBeanHolder beanHolder) {
        return new DefaultFrameworkCallback(messageExecutor, beanHolder);
    }

    @ConditionalOnMissingBean(RobotLogger.class)
    @Bean
    public RobotLogger robotLogger() {
        return new DefaultRobotLogger();
    }
}