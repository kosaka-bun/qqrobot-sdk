package de.honoka.qqrobot.starter;

import de.honoka.qqrobot.starter.component.admin.AdminProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@ComponentScan("de.honoka.qqrobot.starter")
@EnableConfigurationProperties({
        RobotBasicProperties.class, AdminProperties.class
})
@Configuration
public class RobotStarter {}
