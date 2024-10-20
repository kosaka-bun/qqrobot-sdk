package de.honoka.qqrobot.framework.impl.onebot.config

import de.honoka.sdk.util.file.FileUtils
import org.springframework.boot.context.properties.ConfigurationProperties
import kotlin.io.path.Path

@ConfigurationProperties("honoka.qqrobot.onebot")
data class OnebotProperties(

    var host: String? = null,

    var httpPort: Int? = null,

    var websocketPort: Int? = null,
    
    /**
     * 定义缓存文件所存放的目录。<br>
     *
     * 发送图片或文件前，需要先将InputStream中的数据写出到文件中，才能被OneBot服务使用。
     */
    var cachePath: String = Path(FileUtils.getMainClasspath(), "cache").normalize().toString()
) {
    
    val urlPrefix: String
        get() = "http://$host:$httpPort"
    
    val websocketUrlPrefix: String
        get() = "ws://$host:$websocketPort"
    
    val imagePath: String
        get() = Path(cachePath, "image").toString()
    
    val fileToUploadPath: String
        get() = Path(cachePath, "fileToUpload").toString()
}