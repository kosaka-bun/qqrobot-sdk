package de.honoka.qqrobot.framework.config

import de.honoka.qqrobot.framework.config.property.OnebotProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(OnebotProperties::class)
@ComponentScan("de.honoka.qqrobot.framework.impl.onebot")
@ConditionalOnProperty(prefix = "honoka.qqrobot", name = ["framework"], havingValue = "onebot")
@Configuration
class OnebotConfig