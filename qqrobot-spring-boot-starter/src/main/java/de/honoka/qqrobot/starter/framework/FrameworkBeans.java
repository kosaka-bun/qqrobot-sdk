package de.honoka.qqrobot.starter.framework;

import de.honoka.qqrobot.framework.FrameworkCallback;
import de.honoka.qqrobot.starter.framework.mirai.MiraiFramework;
import de.honoka.qqrobot.starter.framework.mirai.config.MiraiProperties;
import de.honoka.qqrobot.starter.framework.tester.TesterFramework;
import de.honoka.qqrobot.starter.framework.tester.component.RobotCallbackAspect;
import de.honoka.qqrobot.starter.framework.tester.config.TesterProperties;
import de.honoka.qqrobot.starter.property.RobotBasicProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class FrameworkBeans {

    @Resource
    private FrameworkCallback frameworkCallback;

    @Resource
    private RobotBasicProperties basicProperties;

    @Resource
    private MiraiProperties miraiProperties;

    @Resource
    private TesterProperties testerProperties;

    @ConditionalOnProperty(prefix = "honoka.qqrobot",
            name = "framework", havingValue = "mirai")
    @Bean
    public MiraiFramework miraiFramework() {
        return new MiraiFramework(frameworkCallback, basicProperties,
                miraiProperties);
    }

    @ConditionalOnProperty(prefix = "honoka.qqrobot",
            name = "framework", havingValue = "tester")
    @Bean
    public TesterFramework testerFramework() {
        return new TesterFramework(frameworkCallback, basicProperties,
                testerProperties);
    }

    @ConditionalOnProperty(prefix = "honoka.qqrobot",
            name = "framework", havingValue = "tester")
    @Bean
    public RobotCallbackAspect robotCallbackAspect() {
        return new RobotCallbackAspect();
    }
}
