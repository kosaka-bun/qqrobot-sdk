package de.honoka.qqrobot.starter;

import de.honoka.qqrobot.starter.component.admin.AdminProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@EnableScheduling
@ComponentScan({ "de.honoka.qqrobot.starter", "de.honoka.qqrobot.framework.config" })
@EnableConfigurationProperties({ RobotBasicProperties.class, AdminProperties.class })
@Configuration
public class RobotStarter {

    public static final ThreadPoolExecutor globalThreadPool = new ThreadPoolExecutor(
        5, 10, 60, TimeUnit.SECONDS,
        new LinkedBlockingDeque<>(20), new ThreadPoolExecutor.CallerRunsPolicy()
    );
}
