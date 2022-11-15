package de.honoka.qqrobot.framework.config;

import de.honoka.qqrobot.framework.impl.tester.config.TesterProperties;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(TesterProperties.class)
@ComponentScan("de.honoka.qqrobot.framework.impl.tester")
@ConditionalOnProperty(prefix = "honoka.qqrobot", name = "framework",
        havingValue = "tester", matchIfMissing = true)
@Configuration
@Data
public class TesterConfig {

    @Value("${server.port}")
    private int serverPort;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    public String getTesterUrl() {
        return "http://localhost:" + serverPort + contextPath +
                TesterProperties.WEB_PREFIX + "/index.html";
    }
}
