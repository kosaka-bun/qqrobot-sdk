package de.honoka.qqrobot.starter.core.logger

import cn.hutool.core.exceptions.ExceptionUtil
import de.honoka.qqrobot.framework.api.RobotFramework
import de.honoka.qqrobot.starter.core.logger.dao.ExceptionRecordDao
import de.honoka.qqrobot.starter.core.logger.dao.UsageLogDao
import de.honoka.qqrobot.starter.core.logger.entity.ExceptionRecord
import de.honoka.qqrobot.starter.core.logger.entity.UsageLog
import de.honoka.sdk.util.kotlin.various.tryBlock
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.util.*

@ConditionalOnMissingBean(RobotLogger::class)
@Component
class DefaultRobotLogger(
    private val usageLogDao: UsageLogDao,
    @param:Lazy
    private val framework: RobotFramework,
    private val exceptionRecordDao: ExceptionRecordDao
) : RobotLogger {

    override fun logException(t: Throwable) {
        tryBlock(3) {
            val record = ExceptionRecord().apply {
                datetime = Date()
                exceptionText = ExceptionUtil.stacktraceToString(t, -1)
            }
            exceptionRecordDao.insert(record)
        }
    }

    override fun logMsgExecution(group: Long, qq: Long, msg: String, reply: String) {
        tryBlock(3) {
            val log = UsageLog().apply {
                groupName = framework.getGroupName(group)
                this.qq = qq
                datetime = Date()
                username = framework.getNickOrCard(group, qq)
                this.msg = msg
                this.reply = reply
            }
            usageLogDao.insert(log)
        }
    }
}
