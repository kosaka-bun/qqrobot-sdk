package de.honoka.qqrobot.framework.impl.tester.server;

import de.honoka.qqrobot.framework.impl.tester.config.TesterProperties;
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
        registry.addResourceHandler(TesterProperties.WEB_PREFIX + "/**")
                .addResourceLocations("classpath:/web/tester-framework/");
    }
}
