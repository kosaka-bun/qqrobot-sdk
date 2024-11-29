package de.honoka.qqrobot.starter.component.logger;

import cn.hutool.core.io.FileUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
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
        String sql = FileUtil.readString(
            Objects.requireNonNull(LoggerServer.class.getResource("/logger/table.sql")),
            StandardCharsets.UTF_8
        );
        log.debug("\nExecute SQL: \n{}", sql);
        try(Connection connection = getConnection();
            Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
