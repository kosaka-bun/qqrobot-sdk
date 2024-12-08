package de.honoka.qqrobot.starter.config.property;

import de.honoka.sdk.util.file.FileUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Paths;

@Getter
@Setter
@ConfigurationProperties("honoka.qqrobot.logger")
public class LoggerProperties {

    private Class<?> databaseDriver = org.h2.Driver.class;

    private String jdbcUrl = "jdbc:h2:" + Paths.get(FileUtils.getMainClasspath(), "qqrobot", "log");
}
