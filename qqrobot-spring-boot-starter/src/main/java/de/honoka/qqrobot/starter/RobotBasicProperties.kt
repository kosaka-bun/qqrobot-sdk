package de.honoka.qqrobot.starter

import org.springframework.boot.context.properties.ConfigurationProperties

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
     * 是否默认启用消息处理功能
     * @see de.honoka.qqrobot.starter.component.RobotStatus.isEnabled
     */
    var defaultEnabled: Boolean = true,
    
    /**
     * 使用哪个框架作为实现（默认为tester）
     */
    var framework: String = "tester",
    
    /**
     * 消息发送失败时是否重发消息
     */
    var resendOnSendFailed: Boolean = false,
    
    /**
     * 重新登录时是否发送测试消息
     */
    var sendTestMessageOnRelogin: Boolean = false
)