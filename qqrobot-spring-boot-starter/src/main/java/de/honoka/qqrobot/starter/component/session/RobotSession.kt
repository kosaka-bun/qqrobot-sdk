package de.honoka.qqrobot.starter.component.session

import de.honoka.qqrobot.framework.api.model.RobotMultipartMessage
import java.io.Closeable
import java.util.concurrent.TimeUnit

/**
 * 会话类，用于记录处于会话状态的qq号和群号，以及这些号码在进入会话状态以后发送的信息
 */
class RobotSession internal constructor(
    val group: Long?,
    val qq: Long,
    /**
     * 所属的会话管理器
     */
    private val sessionManager: SessionManager
) : Closeable {
    
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
    @Volatile
    internal var reply: RobotMultipartMessage? = null
    
    internal lateinit var action: Action
    
    /**
     * 等待某个会话的回复，超时抛出超时异常
     * @param timeout 超时时间，单位为秒
     * @return  回复
     */
    @Throws(TimeoutException::class)
    fun waitingForReply(timeout: Int): RobotMultipartMessage {
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
    
    fun reply(message: RobotMultipartMessage) {
        sessionManager.framework.reply(group, qq, message)
    }
    
    fun reply(message: String) {
        sessionManager.framework.reply(group, qq, message)
    }
    
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
