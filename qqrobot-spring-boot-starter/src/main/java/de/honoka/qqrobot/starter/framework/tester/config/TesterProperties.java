package de.honoka.qqrobot.starter.framework.tester.config;

import de.honoka.sdk.util.file.FileUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Paths;

@Getter
@Setter
@ConfigurationProperties("honoka.qqrobot.tester")
public class TesterProperties {

    private String imagePath = Paths.get(FileUtils.getClasspath(),
            "tester-framework", "image").toString();
}
