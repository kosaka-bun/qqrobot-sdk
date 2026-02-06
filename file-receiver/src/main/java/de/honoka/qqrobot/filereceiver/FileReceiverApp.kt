package de.honoka.qqrobot.filereceiver

import de.honoka.sdk.util.file.FileUtils
import de.honoka.sdk.util.gui.ConsoleWindow
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import java.io.File
import kotlin.io.path.Path

@EnableScheduling
@ConfigurationPropertiesScan
@SpringBootApplication
class FileReceiverApp

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

fun main(args: Array<String>) {
    if(System.getProperty("app.no-gui")?.trim() != "true") {
        ConsoleWindow.Builder.of().run {
            windowName = "File Receiver"
            screenZoomScale = 1.25
            isBackgroundMode = true
            build()
        }
    }
    runApplication<FileReceiverApp>(*args)
}
