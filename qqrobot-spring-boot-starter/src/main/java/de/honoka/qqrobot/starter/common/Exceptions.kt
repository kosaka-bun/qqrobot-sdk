package de.honoka.qqrobot.starter.common

class NoContactException(qq: Long? = null, group: Long? = null) : RuntimeException(buildString {
    qq?.let { append("qq: $it") }
    group?.let { append(", group: $it") }
})

class RobotMutedException(group: Long) : RuntimeException("group: $group")
