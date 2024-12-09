package de.honoka.qqrobot.starter.config.property;

import de.honoka.qqrobot.framework.FrameworkEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("honoka.qqrobot")
@Data
public class RobotBasicProperties {

    private Long qq;

    private String password;

    /**
     * 管理员QQ号
     */
    private Long adminQq;

    /**
     * 开发群群号，可以将机器人的所有工作提示信息（如异常堆栈信息）发至本群
     */
    private Long developingGroup;

    /**
     * 是否需要报告运行时异常
     */
    private boolean reportException = true;

    /**
     * 命令起始字符
     */
    private String commandPrefix = "%";
    
    /**
     * 是否在Spring Boot应用启动完成后自动启动框架
     */
    private boolean autoBoot = true;

    /**
     * 是否默认启用消息处理功能
     * @see de.honoka.qqrobot.starter.component.RobotStatus#isEnabled()
     */
    private boolean defaultEnabled = true;

    /**
     * 消息发送失败时是否重发消息
     */
    private boolean resendOnSendFailed = false;

    /**
     * 重新登录时是否发送测试消息
     */
    private boolean sendTestMessageOnRelogin = false;
    
    private Framework framework = new Framework();
    
    @Data
    public static class Framework {
        
        /**
         * 使用哪个框架作为实现（默认为tester）
         */
        private FrameworkEnum impl = FrameworkEnum.TESTER;
    }
}
