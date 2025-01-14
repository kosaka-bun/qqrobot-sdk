package de.honoka.qqrobot.starter.common

class NoContactException(qq: Long? = null, group: Long? = null) : RuntimeException(buildString {
    qq?.let { append("qq: $qq") }
    group?.let { append(", group: $group") }
})

class RobotMutedException(group: Long) : RuntimeException("group: $group")
