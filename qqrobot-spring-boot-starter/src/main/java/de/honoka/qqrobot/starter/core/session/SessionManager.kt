package de.honoka.qqrobot.starter.core.session

import cn.hutool.core.collection.ConcurrentHashSet
import de.honoka.qqrobot.framework.api.RobotFramework
import de.honoka.sdk.util.various.ThrowsConsumer
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

/**
 * 系统会话列表的管理器
 */
@Component
class SessionManager(
    @param:Lazy
    @JvmSynthetic
    internal val framework: RobotFramework
) {
    
    /**
     * 当前会话列表
     */
    private val sessions = ConcurrentHashSet<RobotSession>()
    
    /**
     * 获取一个会话
     */
    fun getCurrentSession(group: Long?, qq: Long): RobotSession? = sessions.firstOrNull {
        it.group == group && it.qq == qq
    }
    
    fun openSession(group: Long?, qq: Long, configurer: RobotSession.Action.() -> Unit) {
        //检查是否有存在的会话
        getCurrentSession(group, qq)?.let { return }
        val session = RobotSession.of(group, qq, this).apply {
            action = RobotSession.Action()
            action.configurer()
        }
        //添加
        sessions.add(session)
        session.run()
    }
    
    fun openSession(
        group: Long?, qq: Long, action: ThrowsConsumer<RobotSession>,
        onTimeout: ThrowsConsumer<RobotSession>
    ) {
        openSession(group, qq) {
            this.action = {
                action.accept(this)
            }
            this.onTimeout = {
                onTimeout.accept(this)
            }
        }
    }
    
    /**
     * 关闭一个会话
     */
    fun closeSession(group: Long?, qq: Long) {
        getCurrentSession(group, qq)?.let {
            sessions.remove(it)
        }
    }
    
    @JvmSynthetic
    internal fun closeSession(session: RobotSession) {
        sessions.remove(session)
    }
}
