package de.honoka.qqrobot.filereceiver.config

import de.honoka.sdk.util.file.FileUtils
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import java.io.File
import kotlin.io.path.Path

@EnableScheduling
@EnableConfigurationProperties(MainProperties::class)
@Configuration("fileReceiverMainConfig")
class MainConfig

@ConfigurationProperties("file-receiver")
data class MainProperties(
    
    var customPath: String? = null
) {
    
    val filePathPrefix: String = run {
        if(customPath.isNullOrBlank()) {
            return@run Path(FileUtils.getMainClasspath(), "upload").toString()
        }
        val path = File(customPath!!).apply {
            if(!exists()) mkdirs()
            if(!isDirectory) throw Exception("Not directory: $absolutePath")
        }
        path.toPath().normalize().toString()
    }
}