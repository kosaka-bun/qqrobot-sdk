package de.honoka.qqrobot.framework.config.property;

import lombok.Data;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("honoka.qqrobot.mirai")
@Data
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
     */
    private BotConfiguration.MiraiProtocol protocol = BotConfiguration.MiraiProtocol.ANDROID_PAD;
}
