package de.honoka.qqrobot.framework.config.property;

import de.honoka.sdk.util.file.FileUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Paths;

@ConfigurationProperties("honoka.qqrobot.framework.tester")
@Data
public class TesterProperties {

    public static final String WEB_PREFIX = "/tester-framework";

    private String imagePath = Paths.get(FileUtils.getMainClasspath(), "tester-framework", "image").toString();

    private Long groupNumber = 10000L;
}
