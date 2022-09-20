package de.honoka.qqrobot.starter.framework;

import de.honoka.qqrobot.framework.FrameworkCallback;
import de.honoka.qqrobot.starter.RobotBasicProperties;
import de.honoka.qqrobot.starter.framework.mirai.MiraiFramework;
import de.honoka.qqrobot.starter.framework.mirai.config.MiraiProperties;
import de.honoka.qqrobot.starter.framework.tester.TesterFramework;
import de.honoka.qqrobot.starter.framework.tester.component.RobotCallbackAspect;
import de.honoka.qqrobot.starter.framework.tester.config.TesterProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FrameworkBeans {

    @ConditionalOnProperty(prefix = "honoka.qqrobot",
            name = "framework", havingValue = "mirai")
    @Bean
    public MiraiFramework miraiFramework(
            FrameworkCallback frameworkCallback,
            RobotBasicProperties basicProperties,
            MiraiProperties miraiProperties) {
        return new MiraiFramework(frameworkCallback, basicProperties,
                miraiProperties);
    }

    @ConditionalOnProperty(prefix = "honoka.qqrobot", name = "framework",
            havingValue = "tester", matchIfMissing = true)
    @Bean
    public TesterFramework testerFramework(
            FrameworkCallback frameworkCallback,
            RobotBasicProperties basicProperties,
            TesterProperties testerProperties) {
        return new TesterFramework(frameworkCallback, basicProperties,
                testerProperties);
    }

    @ConditionalOnProperty(prefix = "honoka.qqrobot", name = "framework",
            havingValue = "tester", matchIfMissing = true)
    @Bean
    public RobotCallbackAspect robotCallbackAspect() {
        return new RobotCallbackAspect();
    }
}
