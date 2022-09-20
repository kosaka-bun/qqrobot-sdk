package de.honoka.qqrobot.starter.framework.mirai.config;

import de.honoka.qqrobot.starter.framework.mirai.MiraiFramework;
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
     * 采用的登录协议
     * @see MiraiFramework#setProtocol(BotConfiguration, String)
     */
    private String protocol = "android_pad";
}
