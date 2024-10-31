package de.honoka.qqrobot.filereceiver.common

import de.honoka.qqrobot.filereceiver.config.MainProperties
import de.honoka.sdk.util.file.FileUtils
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import kotlin.io.path.Path

@Component
class ScheduledTasks(private val mainProperties: MainProperties) {
    
    private val subDirToCheckNameList = listOf("image", "file")

    @Scheduled(cron = "0 */10 * * * ?")
    fun cleanReceivedFiles() = subDirToCheckNameList.forEach {
        val dir = Path(mainProperties.filePathPrefix, it).toFile()
        if(!dir.exists() || !dir.isDirectory) return@forEach
        dir.listFiles()?.forEach { f ->
            val createTime = FileUtils.getCreateTime(f).time
            if(System.currentTimeMillis() - createTime > 9 * 1000L) {
                runCatching {
                    f.delete()
                }
            }
        }
    }
}