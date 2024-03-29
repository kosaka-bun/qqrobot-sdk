package de.honoka.qqrobot.starter.component.logger;

import de.honoka.sdk.util.file.FileUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Objects;

@Slf4j
@EnableConfigurationProperties(LoggerProperties.class)
@Component
public class LoggerServer {

    @Resource
    private LoggerProperties loggerProperties;

    @SneakyThrows
    public Connection getConnection() {
        Connection connection = DriverManager.getConnection(
                loggerProperties.getJdbcUrl());
        connection.setAutoCommit(true);
        return connection;
    }

    @PostConstruct
    public void init() {
        log.info("Logger Data Source: " + loggerProperties.getJdbcUrl());
        createTable();
    }

    @SneakyThrows
    public void createTable() {
        String sql = FileUtils.urlToString(Objects.requireNonNull(
                LoggerServer.class.getResource("/logger/table.sql")
        ));
        log.debug("\nExecute SQL: \n" + sql);
        try(Connection connection = getConnection();
            Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
