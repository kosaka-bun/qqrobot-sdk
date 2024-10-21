package de.honoka.qqrobot.framework.impl.onebot.config

import kotlin.io.path.Path

val OnebotProperties.urlPrefix: String
    get() = "http://$host:$httpPort"

val OnebotProperties.websocketUrlPrefix: String
    get() = "ws://$host:$websocketPort"

val OnebotProperties.imagePath: String
    get() = Path(cachePath, "image").toString()

val OnebotProperties.fileToUploadPath: String
    get() = Path(cachePath, "fileToUpload").toString()