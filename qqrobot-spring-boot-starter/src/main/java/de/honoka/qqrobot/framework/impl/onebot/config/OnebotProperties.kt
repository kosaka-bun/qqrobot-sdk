package de.honoka.qqrobot.framework.impl.onebot.config

import kotlin.io.path.Path

//这些属性不能直接放在OnebotProperties类中作为字段，会被解析为Spring Boot配置项

val OnebotProperties.urlPrefix: String
    get() = "http://$host:$httpPort"

val OnebotProperties.websocketUrlPrefix: String
    get() = "ws://$host:$websocketPort"

val OnebotProperties.fileReceiverUrlPrefix: String
    get() = "http://$host:$fileReceiverPort"

val OnebotProperties.imagePath: String
    get() = Path(cachePath, "image").toString()

val OnebotProperties.fileToUploadPath: String
    get() = Path(cachePath, "fileToUpload").toString()