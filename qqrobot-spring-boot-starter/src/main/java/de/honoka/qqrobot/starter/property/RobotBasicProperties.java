package de.honoka.qqrobot.starter.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
@Getter
@Setter
@ConfigurationProperties("honoka.qqrobot")
public class RobotBasicProperties {

    @NotNull(message = "要登录的qq号不能为空")
    private Long qq;

    @NotNull(message = "要登录的qq密码不能为空")
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
    private Boolean reportException = true;

    /**
     * 命令起始字符
     */
    private String commandPrefix = "%";

    /**
     * 是否默认启用消息处理功能
     */
    private Boolean defaultEnabled = true;

    /**
     * 使用哪个框架作为实现（默认为tester）
     */
    private String framework = "tester";
}
