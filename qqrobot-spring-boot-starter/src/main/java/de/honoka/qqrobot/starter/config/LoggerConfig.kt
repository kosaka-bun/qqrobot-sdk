package de.honoka.qqrobot.starter.config

import de.honoka.sdk.util.file.FileUtils
import org.h2.Driver
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import kotlin.io.path.Path

@Configuration
class LoggerConfig

@ConfigurationProperties("honoka.qqrobot.logger")
data class LoggerProperties(
    
    var databaseDriver: Class<*> = Driver::class.java,
    
    var jdbcUrl: String = "jdbc:h2:${Path(FileUtils.getMainClasspath(), "qqrobot", "log")}"
)
