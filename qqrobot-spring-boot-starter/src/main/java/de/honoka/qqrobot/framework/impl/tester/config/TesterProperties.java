package de.honoka.qqrobot.framework.impl.tester.config;

import de.honoka.sdk.util.file.FileUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Paths;

@Getter
@Setter
@ConfigurationProperties("honoka.qqrobot.tester")
public class TesterProperties {

    public static final String WEB_PREFIX = "/tester-framework";

    private String imagePath = Paths.get(FileUtils.getMainClasspath(), "tester-framework", "image").toString();

    private Long groupNumber = 10000L;
}
