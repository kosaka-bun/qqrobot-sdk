package de.honoka.qqrobot.starter.config

import de.honoka.qqrobot.starter.framework.FrameworkEnum
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(RobotBasicProperties::class)
@Configuration
class RobotBasicConfig

@ConfigurationProperties("honoka.qqrobot")
data class RobotBasicProperties(
    
    var qq: Long? = null,
    
    var password: String? = null,
    
    /**
     * 管理员QQ号
     */
    var adminQq: Long? = null,
    
    /**
     * 开发群群号，可以将机器人的所有工作提示信息（如异常堆栈信息）发至本群
     */
    var developingGroup: Long? = null,
    
    /**
     * 是否需要报告运行时异常
     */
    var reportException: Boolean = true,
    
    /**
     * 命令起始字符
     */
    var commandPrefix: String = "%",
    
    /**
     * 是否在Spring Boot应用启动完成后自动启动框架
     */
    var autoBoot: Boolean = true,
    
    /**
     * 是否默认启用消息处理功能
     *
     * @see de.honoka.qqrobot.starter.core.RobotStatus.isEnabled
     */
    var defaultEnabled: Boolean = true,
    
    /**
     * 消息发送失败时是否重发消息
     */
    var resendOnSendFailed: Boolean = false,
    
    /**
     * 重新登录时是否发送测试消息
     */
    var sendTestMessageOnRelogin: Boolean = false,
    
    var framework: Framework = Framework()
) {
    
    data class Framework(
        
        /**
         * 使用哪个框架作为实现（默认为tester）
         */
        var impl: FrameworkEnum = FrameworkEnum.TESTER
    )
}
