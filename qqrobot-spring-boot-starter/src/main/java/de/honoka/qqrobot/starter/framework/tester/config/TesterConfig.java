package de.honoka.qqrobot.starter.framework.tester.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Data
@EnableConfigurationProperties(TesterProperties.class)
@Configuration
public class TesterConfig {

    @Value("${server.port}")
    private int serverPort;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Resource
    private TesterProperties testerProperties;

    public String getTesterUrl() {
        return "http://localhost:" + serverPort + contextPath +
                testerProperties.getWebPrefix() + "/index.html";
    }
}
