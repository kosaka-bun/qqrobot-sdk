package de.honoka.qqrobot.starter.framework.tester.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class TesterServerConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Bean
    public TesterServerConnection testerServerConnection(TesterServer server) {
        return new TesterServerConnection(server);
    }
}
