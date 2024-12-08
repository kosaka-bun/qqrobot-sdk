package de.honoka.qqrobot.starter;

import de.honoka.qqrobot.starter.config.property.AdminProperties;
import de.honoka.qqrobot.starter.config.property.RobotBasicProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@EnableScheduling
@ComponentScan({ "de.honoka.qqrobot.starter", "de.honoka.qqrobot.framework.config" })
@EnableConfigurationProperties({ RobotBasicProperties.class, AdminProperties.class })
@Configuration
public class RobotStarter {

    //用于执行非即时任务（多而密集，但不需要尽快完成）
    public static final ThreadPoolExecutor globalThreadPool = new ThreadPoolExecutor(
        3, 10, 10, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(10), new ThreadPoolExecutor.AbortPolicy()
    );
    
    //用于执行即时任务（大部分时候都少而不密集，需要尽快完成）
    public static final ThreadPoolExecutor globalInstantThreadPool = new ThreadPoolExecutor(
        1, 20, 60, TimeUnit.SECONDS,
        new SynchronousQueue<>(), new ThreadPoolExecutor.AbortPolicy()
    );
}
