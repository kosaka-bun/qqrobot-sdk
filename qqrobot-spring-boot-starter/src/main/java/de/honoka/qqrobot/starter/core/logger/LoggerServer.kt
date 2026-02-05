package de.honoka.qqrobot.starter.core.logger

import de.honoka.qqrobot.starter.config.LoggerProperties
import de.honoka.sdk.util.kotlin.various.log
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.DriverManager

@Component
class LoggerServer(private val loggerProperties: LoggerProperties) {
    
    val connection: Connection
        get() {
            Class.forName(loggerProperties.databaseDriver.name)
            return DriverManager.getConnection(loggerProperties.jdbcUrl).apply {
                autoCommit = true
            }
        }
    
    @PostConstruct
    fun init() {
        log.info("Logger Data Source: ${loggerProperties.jdbcUrl}")
        createTable()
    }
    
    fun createTable() {
        val sql = LoggerServer::class.java.getResource("/logger/table.sql")!!.readText()
        log.debug("\nExecute SQL: \n$sql")
        connection.use {
            it.createStatement().use { st ->
                st.execute(sql)
            }
        }
    }
}
