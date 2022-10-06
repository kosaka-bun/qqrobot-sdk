package de.honoka.qqrobot.demo;

import de.honoka.qqrobot.framework.Framework;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class DemoConfig implements ApplicationRunner {

    @Resource
    private Framework<?> framework;

    @Override
    public void run(ApplicationArguments args) {
        framework.boot();
    }
}
