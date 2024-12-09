package de.honoka.qqrobot.starter.config.property;

import de.honoka.sdk.util.file.FileUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Paths;

@ConfigurationProperties("honoka.qqrobot.logger")
@Data
public class LoggerProperties {

    private Class<?> databaseDriver = org.h2.Driver.class;

    private String jdbcUrl = "jdbc:h2:" + Paths.get(FileUtils.getMainClasspath(), "qqrobot", "log");
}
