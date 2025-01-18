package de.honoka.qqrobot.framework.api.message

import java.io.InputStream

object RobotMessageTypes {
    
    data class At(val qq: Long)
    
    data class Image(val content: InputStream)
    
    data class File(val content: InputStream)
}
