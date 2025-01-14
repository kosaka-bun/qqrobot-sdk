package de.honoka.qqrobot.starter.component

import cn.hutool.cache.impl.TimedCache
import cn.hutool.core.exceptions.ExceptionUtil
import de.honoka.qqrobot.framework.ExtendedRobotFramework
import de.honoka.qqrobot.framework.api.model.RobotMessage
import de.honoka.qqrobot.framework.api.model.RobotMultipartMessage
import de.honoka.qqrobot.starter.component.logger.RobotLogger
import de.honoka.qqrobot.starter.config.RobotBasicProperties
import de.honoka.qqrobot.starter.util.GlobalThreadPools
import de.honoka.sdk.util.kotlin.basic.log
import de.honoka.sdk.util.kotlin.concurrent.ScheduledTask
import de.honoka.sdk.util.various.ImageUtils
import org.springframework.stereotype.Component
import java.util.concurrent.LinkedBlockingQueue
import kotlin.reflect.KClass

@Component
class ExceptionReporter(
    private val basicProperties: RobotBasicProperties,
    private val framework: ExtendedRobotFramework,
    private val robotLogger: RobotLogger
) {
    
    private val scheduledTask = ScheduledTask("10s", action = ::doTask)
    
    private val exceptionQueue = LinkedBlockingQueue<Throwable>()
    
    private val exceptionCache = TimedCache<KClass<out Throwable>, Unit>(10 * 1000)
    
    init {
        if(basicProperties.reportException) {
            scheduledTask.startup()
        }
    }
    
    private fun doTask() {
        val throwable = exceptionQueue.take()
        val reply = RobotMultipartMessage.of("出现了问题，堆栈信息如下：\n").apply {
            val text = ExceptionUtil.stacktraceToString(throwable).run {
                val lines = lines().run {
                    if(size < 20) this else subList(0, 20)
                }
                lines.joinToString("\n")
            }
            val image = ImageUtils.textToImageByLength(text, 120)
            add(RobotMessage.image(image))
        }
        framework.sendMsgToDevelopingGroup(reply)
    }

    fun report(t: Throwable) {
        log.error("", t)
        GlobalThreadPools.pool.submit {
            doReport(t)
        }
    }
    
    private fun doReport(t: Throwable) {
        val cause = ExceptionUtil.getRootCause(t)
        synchronized(this) {
            val clazz = cause::class
            if(exceptionCache.containsKey(clazz)) return
            exceptionCache.put(clazz, Unit)
        }
        runCatching {
            robotLogger.logException(cause)
        }
        if(!basicProperties.reportException) return
        exceptionQueue.offer(cause)
    }
}
