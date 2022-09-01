package de.honoka.qqrobot.spring.boot.starter.framework.mirai;

import de.honoka.qqrobot.framework.Framework;
import de.honoka.qqrobot.framework.Robot;
import de.honoka.qqrobot.spring.boot.starter.framework.mirai.property.MiraiProperties;
import de.honoka.qqrobot.spring.boot.starter.property.RobotBasicProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@EnableConfigurationProperties(MiraiProperties.class)
@Configuration
public class MiraiConfiguration {

    @Resource
    private RobotBasicProperties basicProperties;

    @Resource
    private MiraiProperties miraiProperties;

    @Resource
    private Robot robot;

    @Bean
    public Framework miraiFramework() {
        return new MiraiFramework(robot, basicProperties, miraiProperties);
    }
}