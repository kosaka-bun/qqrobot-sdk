package de.honoka.qqrobot.filereceiver

import de.honoka.sdk.util.system.gui.ConsoleWindow
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FileReceiverApplication

fun main(args: Array<String>) {
    if(System.getProperty("app.no-gui")?.trim() != "true") {
        ConsoleWindow.Builder.of().run {
            windowName = "File Receiver"
            screenZoomScale = 1.25
            isBackgroundMode = true
            build()
        }
    }
    runApplication<FileReceiverApplication>(*args)
}