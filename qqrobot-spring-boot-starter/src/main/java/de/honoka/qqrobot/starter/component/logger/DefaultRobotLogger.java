package de.honoka.qqrobot.starter.component.logger;

import de.honoka.qqrobot.framework.Framework;
import de.honoka.qqrobot.framework.model.RobotMultipartMessage;
import de.honoka.qqrobot.starter.common.ConditionalBeans;
import de.honoka.qqrobot.starter.common.RobotBeanHolder;
import de.honoka.qqrobot.starter.common.annotation.ConditionalComponent;
import de.honoka.qqrobot.starter.component.logger.dao.ExceptionRecordDao;
import de.honoka.qqrobot.starter.component.logger.dao.UsageLogDao;
import de.honoka.qqrobot.starter.component.logger.entity.ExceptionRecord;
import de.honoka.qqrobot.starter.component.logger.entity.UsageLog;
import de.honoka.sdk.util.text.ExceptionUtils;
import de.honoka.sdk.util.various.Retrier;

import javax.annotation.Resource;
import java.util.Date;

@ConditionalComponent(ConditionalBeans.class)
public class DefaultRobotLogger implements RobotLogger {

    @Resource
    private UsageLogDao usageLogDao;

    @Resource
    private RobotBeanHolder robotBeanHolder;

    @Resource
    private ExceptionRecordDao exceptionRecordDao;

    @Override
    public void logException(Throwable t) {
        String str = ExceptionUtils.transfer(t);
        new Retrier().tryCode(() -> {
            exceptionRecordDao.insert(new ExceptionRecord()
                    .setDatetime(new Date())
                    .setExceptionText(str)
            );
        });
    }

    @Override
    public void logMsgExecution(Long group, long qq,
                                RobotMultipartMessage msg,
                                RobotMultipartMessage reply) {
        new Retrier().tryCode(() -> {
            Framework<?> framework = robotBeanHolder.getFramework();
            usageLogDao.insert(new UsageLog()
                    .setGroupName(framework.getGroupName(group))
                    .setQq(qq).setDatetime(new Date())
                    .setUsername(framework.getNickOrCard(group, qq))
                    .setMsg(msg.contentToString())
                    .setReply(reply.contentToString())
            );
        });
    }
}
