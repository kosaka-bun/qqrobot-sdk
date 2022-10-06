package de.honoka.qqrobot.starter.component.admin;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("honoka.qqrobot.admin")
public class AdminProperties {

    public static final String WEB_PREFIX = "/admin";

    /**
     * 后台管理界面的登录密码
     */
    private String password = "123456";
}
