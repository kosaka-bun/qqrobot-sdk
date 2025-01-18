package de.honoka.qqrobot.starter.command

object CommandExceptions {
    
    class WrongNumberParameterException(message: String? = null) : RuntimeException(message)
    
    class WrongAtParameterException(message: String? = null) : RuntimeException(message)
    
    class IndexOutOfBoundsException(
        index: Int, maxIndex: Int
    ) : RuntimeException("index: $index, maxIndex: $maxIndex")
}
