package de.honoka.qqrobot.framework.config

import net.mamoe.mirai.utils.BotConfiguration.MiraiProtocol
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(MiraiProperties::class)
@ComponentScan("de.honoka.qqrobot.framework.impl.mirai")
@ConditionalOnProperty(prefix = "honoka.qqrobot.framework", name = ["impl"], havingValue = "mirai")
@Configuration
class MiraiConfig 

@ConfigurationProperties("honoka.qqrobot.framework.mirai")
data class MiraiProperties(
    
    /**
     * 平台输出与依赖文件的存放目录
     */
    var workDirectory: String = "/qqrobot/mirai",
    
    /**
     * 是否转移日志到文件中
     */
    var redirectLogs: Boolean = false,
    
    /**
     * 采用的登录协议
     */
    var protocol: MiraiProtocol = MiraiProtocol.ANDROID_PAD
)