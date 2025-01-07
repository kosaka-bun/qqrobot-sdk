package de.honoka.qqrobot.starter.component.logger

import cn.hutool.core.io.FileUtil
import de.honoka.qqrobot.starter.config.LoggerProperties
import de.honoka.sdk.util.kotlin.basic.log
import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.sql.Connection
import java.sql.DriverManager

@EnableConfigurationProperties(LoggerProperties::class)
@Component
class LoggerServer(private val loggerProperties: LoggerProperties) {
    
    val connection: Connection
        get() {
            Class.forName(loggerProperties.databaseDriver.name)
            val c = DriverManager.getConnection(loggerProperties.jdbcUrl).apply {
                autoCommit = true
            }
            return c
        }
    
    @PostConstruct
    fun init() {
        log.info("Logger Data Source: ${loggerProperties.jdbcUrl}")
        createTable()
    }
    
    fun createTable() {
        val sql = FileUtil.readString(
            LoggerServer::class.java.getResource("/logger/table.sql"),
            StandardCharsets.UTF_8
        )
        log.debug("\nExecute SQL: \n$sql")
        connection.use {
            it.createStatement().use { st ->
                st.execute(sql)
            }
        }
    }
}
