package de.honoka.qqrobot.framework.config;

import de.honoka.qqrobot.framework.impl.mirai.config.MiraiProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(MiraiProperties.class)
@ComponentScan("de.honoka.qqrobot.framework.impl.mirai")
@ConditionalOnProperty(prefix = "honoka.qqrobot", name = "framework",
        havingValue = "mirai")
@Configuration
public class MiraiConfig {}
