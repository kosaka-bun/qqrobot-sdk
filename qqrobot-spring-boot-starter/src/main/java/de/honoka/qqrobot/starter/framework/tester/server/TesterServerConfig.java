package de.honoka.qqrobot.starter.framework.tester.server;

import de.honoka.qqrobot.starter.framework.tester.config.TesterProperties;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.annotation.Resource;

@Getter
@Configuration
public class TesterServerConfig implements WebMvcConfigurer {

    @Resource
    private TesterProperties testerProperties;

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Bean
    public TesterServerConnection testerServerConnection(TesterServer server) {
        return new TesterServerConnection(server);
    }

    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler(testerProperties
                .getWebPrefix() + "/**"
        ).addResourceLocations("classpath:/web/tester-framework/");
    }
}
