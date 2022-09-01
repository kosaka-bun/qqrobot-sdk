package de.honoka.qqrobot.spring.boot.starter.framework.mirai.property;

import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("honoka.qqrobot.mirai")
public class MiraiProperties {

    /**
     * 平台输出与依赖文件的存放目录
     */
    private String workDirectory = "/qqrobot/mirai";

    /**
     * 是否转移日志到文件中
     */
    private Boolean redirectLogs = false;

    /**
     * 消息发送失败时是否重发消息
     */
    private Boolean resendOnSendFailed = false;

    /**
     * 重新登录时是否发送测试消息
     */
    private Boolean sendTestMessageOnRelogin = false;

    /**
     * 采用的登录协议
     * @see de.honoka.qqrobot.spring.boot.starter.framework.mirai.MiraiFramework#setProtocol(BotConfiguration, String)
     */
    private String protocol = "android_pad";
}
