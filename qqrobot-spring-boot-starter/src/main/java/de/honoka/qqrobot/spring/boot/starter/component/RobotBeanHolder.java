package de.honoka.qqrobot.spring.boot.starter.component;

import de.honoka.qqrobot.framework.Framework;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RobotBeanHolder {

    @Resource
    private ApplicationContext applicationContext;

    private Framework framework;

    public Framework getFramework() {
        if(framework != null) return framework;
        synchronized(this) {
            if(framework != null) return framework;
            framework = applicationContext.getBean(Framework.class);
            return framework;
        }
    }
}
