package de.honoka.qqrobot.starter.component.logger

import cn.hutool.core.io.FileUtil
import de.honoka.sdk.util.file.FileUtils
import de.honoka.sdk.util.kotlin.basic.log
import jakarta.annotation.PostConstruct
import org.h2.Driver
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.sql.Connection
import java.sql.DriverManager
import kotlin.io.path.Path

@EnableConfigurationProperties(LoggerProperties::class)
@Component
class LoggerServer(private val loggerProperties: LoggerProperties) {
    
    val connection: Connection
        get() = DriverManager.getConnection(loggerProperties.jdbcUrl).apply {
            autoCommit = true
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

@ConfigurationProperties("honoka.qqrobot.logger")
class LoggerProperties(
    
    var databaseDriver: Class<*> = Driver::class.java,
    
    var jdbcUrl: String = "jdbc:h2:${Path(FileUtils.getMainClasspath(), "qqrobot", "log")}"
)