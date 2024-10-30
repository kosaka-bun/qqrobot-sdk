package de.honoka.qqrobot.filereceiver.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(MainProperties::class)
@Configuration
class MainConfig

@ConfigurationProperties("file-receiver")
data class MainProperties(
    
    var customPath: String? = null
)