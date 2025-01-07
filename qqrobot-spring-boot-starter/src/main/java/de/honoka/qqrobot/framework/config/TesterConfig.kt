package de.honoka.qqrobot.framework.config

import de.honoka.sdk.util.file.FileUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import kotlin.io.path.Path

@EnableConfigurationProperties(TesterProperties::class)
@ComponentScan("de.honoka.qqrobot.framework.impl.tester")
@ConditionalOnProperty(
    prefix = "honoka.qqrobot.framework",
    name = ["impl"],
    havingValue = "tester",
    matchIfMissing = true
)
@Configuration
class TesterConfig {
    
    @Value("\${server.port:8080}")
    var serverPort = 0
    
    @Value("\${server.servlet.context-path:}")
    var contextPath = "/"
    
    val testerUrl: String
        get() = "http://localhost:$serverPort$contextPath${TesterProperties.WEB_PREFIX}/index.html"
}

@ConfigurationProperties("honoka.qqrobot.framework.tester")
data class TesterProperties(
    
    var groupNumber: Long = 10000L,
    
    var imagePath: String = run {
        Path(FileUtils.getMainClasspath(), "tester-framework", "image").toString()
    }
) {
    
    companion object {
        
        const val WEB_PREFIX = "/tester-framework"
    }
}
