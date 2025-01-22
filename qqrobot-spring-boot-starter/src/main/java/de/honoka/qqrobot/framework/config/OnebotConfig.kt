package de.honoka.qqrobot.framework.config

import de.honoka.sdk.util.file.FileUtils
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import kotlin.io.path.Path

@EnableConfigurationProperties(OnebotProperties::class)
@ComponentScan("de.honoka.qqrobot.framework.impl.onebot")
@ConditionalOnProperty(prefix = "honoka.qqrobot.framework", name = ["impl"], havingValue = "onebot")
@Configuration
class OnebotConfig

@ConfigurationProperties("honoka.qqrobot.framework.onebot")
data class OnebotProperties(
    
    var host: String? = null,
    
    var httpPort: Int? = null,
    
    var websocketPort: Int? = null,
    
    var fileReceiverPort: Int? = null,
    
    /**
     * 定义缓存文件所存放的目录。
     *
     * 发送图片或文件前，需要先将InputStream中的数据写出到文件中，才能被OneBot服务使用。
     */
    var cachePath: String = Path(FileUtils.getMainClasspath(), "qqrobot/onebot/cache").run {
        normalize().toString()
    }
) {
    
    val urlPrefix: String
        get() = "http://$host:$httpPort"
    
    val websocketUrlPrefix: String
        get() = "ws://$host:$websocketPort"
    
    val fileReceiverUrlPrefix: String
        get() = "http://$host:$fileReceiverPort"
    
    val imagePath: String
        get() = Path(cachePath, "image").toString()
    
    val fileToUploadPath: String
        get() = Path(cachePath, "fileToUpload").toString()
}
