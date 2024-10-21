package de.honoka.qqrobot.starter;

import de.honoka.qqrobot.starter.component.admin.AdminProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@EnableScheduling
@ComponentScan({ "de.honoka.qqrobot.starter", "de.honoka.qqrobot.framework.config" })
@EnableConfigurationProperties({ RobotBasicProperties.class, AdminProperties.class })
@Configuration
public class RobotStarter {

    //用于执行杂类任务（非即时任务）
    public static final ThreadPoolExecutor globalThreadPool = new ThreadPoolExecutor(
        3, 20, 60, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(3), new ThreadPoolExecutor.AbortPolicy()
    );
}
