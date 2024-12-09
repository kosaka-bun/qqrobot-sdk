package de.honoka.qqrobot.starter.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("honoka.qqrobot.admin")
@Data
public class AdminProperties {

    public static final String WEB_PREFIX = "/admin";

    /**
     * 后台管理界面的登录密码
     */
    private String password = "123456";
}
