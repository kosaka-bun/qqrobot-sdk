package de.honoka.qqrobot.starter;

import de.honoka.qqrobot.starter.property.RobotBasicProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@ComponentScan({
        "de.honoka.qqrobot.starter.component",
        "de.honoka.qqrobot.starter.framework"
})
@EnableConfigurationProperties(RobotBasicProperties.class)
@Configuration
public class RobotStarter {}
