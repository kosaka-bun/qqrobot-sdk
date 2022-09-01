package de.honoka.qqrobot.spring.boot.starter.property;

import de.honoka.sdk.util.file.FileUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.nio.file.Path;

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
     * 生成的消息图片的保存目录
     */
    private String messageImagePath = Path.of(FileUtils.getClasspath(),
            "/qqrobot/image").toString();

    /**
     * 是否默认启用消息处理功能
     */
    private Boolean defaultEnabled = true;

    //自动为输入的路径添加classpath路径，所以不需要手动填写classpath
    public void setMessageImagePath(String messageImagePath) {
        this.messageImagePath = Path.of(FileUtils.getClasspath(),
                messageImagePath).toString();
    }
}
