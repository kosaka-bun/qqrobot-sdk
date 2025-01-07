package de.honoka.qqrobot.starter.config

import de.honoka.sdk.util.file.FileUtils
import org.h2.Driver
import org.springframework.boot.context.properties.ConfigurationProperties
import kotlin.io.path.Path

@ConfigurationProperties("honoka.qqrobot.logger")
class LoggerProperties(
    
    var databaseDriver: Class<*> = Driver::class.java,
    
    var jdbcUrl: String = "jdbc:h2:${Path(FileUtils.getMainClasspath(), "qqrobot", "log")}"
)
