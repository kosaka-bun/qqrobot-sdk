package de.honoka.qqrobot.starter.component.admin;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("honoka.qqrobot.admin")
public class AdminProperties {

    private String password = "123456";

    private String webPrefix = "/admin";
}
