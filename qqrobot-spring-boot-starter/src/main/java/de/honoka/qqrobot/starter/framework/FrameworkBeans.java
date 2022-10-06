package de.honoka.qqrobot.starter.framework;

import de.honoka.qqrobot.starter.framework.mirai.MiraiFramework;
import de.honoka.qqrobot.starter.framework.tester.TesterFramework;
import de.honoka.qqrobot.starter.framework.tester.component.RobotCallbackAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FrameworkBeans {

    @ConditionalOnProperty(prefix = "honoka.qqrobot",
            name = "framework", havingValue = "mirai")
    @Bean
    public MiraiFramework miraiFramework() {
        return new MiraiFramework();
    }

    @ConditionalOnProperty(prefix = "honoka.qqrobot", name = "framework",
            havingValue = "tester", matchIfMissing = true)
    @Bean
    public TesterFramework testerFramework() {
        return new TesterFramework();
    }

    @ConditionalOnProperty(prefix = "honoka.qqrobot", name = "framework",
            havingValue = "tester", matchIfMissing = true)
    @Bean
    public RobotCallbackAspect robotCallbackAspect() {
        return new RobotCallbackAspect();
    }
}
