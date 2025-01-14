package de.honoka.qqrobot.starter.component.session

import de.honoka.qqrobot.framework.api.model.RobotMultipartMessage
import java.io.Closeable
import java.util.concurrent.TimeUnit

/**
 * 会话类，用于记录处于会话状态的qq号和群号，以及这些号码在进入会话状态以后发送的信息
 */
class RobotSession private constructor(
    val group: Long?,
    val qq: Long,
    /**
     * 所属的会话管理器
     */
    private val sessionManager: SessionManager
) : Closeable {
    
    companion object {
        
        @JvmSynthetic
        internal fun of(group: Long?, qq: Long, sessionManager: SessionManager): RobotSession = run {
            RobotSession(group, qq, sessionManager)
        }
    }
    
    data class Action(
        
        /**
         * 会话过程中要执行的操作
         */
        var action: (RobotSession.() -> Unit)? = null,
        
        /**
         * 超时操作
         */
        var onTimeout: (RobotSession.() -> Unit)? = null
    )
    
    class TimeoutException : Exception()
    
    @set:JvmName("setReply")
    @get:JvmSynthetic
    @Volatile
    internal var reply: RobotMultipartMessage? = null
    
    @set:JvmSynthetic
    @get:JvmSynthetic
    internal lateinit var action: Action
    
    /**
     * 等待某个会话的回复，超时抛出超时异常
     * @param timeout 超时时间，单位为秒
     * @return  回复
     */
    @Throws(TimeoutException::class)
    fun waitForReply(timeout: Int): RobotMultipartMessage {
        //等待回复前，先忽略已有的回复
        reply = null
        var i = 0
        while(reply == null) {
            TimeUnit.SECONDS.sleep(1)
            i++
            if(i >= timeout) throw TimeoutException()
        }
        return reply!!
    }
    
    inline fun waitForReply(
        prompt: String,
        promptOnInvalidValue: String = "提供的参数有误，请重新输入",
        resultPredicate: (String) -> Boolean = { true },
        timeout: Int = 60
    ): String {
        reply(prompt)
        var result: String
        while(true) {
            try {
                result = waitForReply(timeout).contentToString()
                val isValid = runCatching { resultPredicate(result) }.getOrDefault(false)
                if(isValid) break
                reply(promptOnInvalidValue)
            } catch(t: Throwable) {
                if(t is TimeoutException) throw t
                reply(promptOnInvalidValue)
            }
        }
        return result
    }
    
    fun reply(message: RobotMultipartMessage) {
        sessionManager.framework.reply(group, qq, message)
    }
    
    fun reply(message: String) {
        sessionManager.framework.reply(group, qq, message)
    }
    
    @JvmSynthetic
    internal fun run() {
        try {
            action.action!!(this)
        } catch(t: Throwable) {
            if(t !is TimeoutException) throw t
            if(action.onTimeout != null) {
                action.onTimeout!!(this)
            } else {
                sessionManager.framework.reply(group, qq, "会话已超时关闭")
            }
        } finally {
            close()
        }
    }
    
    override fun close() {
        sessionManager.closeSession(this)
    }
}
