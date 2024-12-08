package de.honoka.qqrobot.starter.component.logger;

import de.honoka.qqrobot.framework.api.Framework;
import de.honoka.qqrobot.framework.api.model.RobotMultipartMessage;
import de.honoka.qqrobot.starter.common.annotation.ConditionalComponent;
import de.honoka.qqrobot.starter.component.logger.dao.ExceptionRecordDao;
import de.honoka.qqrobot.starter.component.logger.dao.UsageLogDao;
import de.honoka.qqrobot.starter.component.logger.entity.ExceptionRecord;
import de.honoka.qqrobot.starter.component.logger.entity.UsageLog;
import de.honoka.qqrobot.starter.config.ConditionalBeansConfig;
import de.honoka.sdk.util.text.ExceptionUtils;
import de.honoka.sdk.util.various.Retrier;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;

import java.util.Date;

@ConditionalComponent(ConditionalBeansConfig.class)
public class DefaultRobotLogger implements RobotLogger {

    @Resource
    private UsageLogDao usageLogDao;

    @Lazy
    @Resource
    private Framework<?> framework;

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
    public void logMsgExecution(
        Long group, long qq, RobotMultipartMessage msg, RobotMultipartMessage reply
    ) {
        new Retrier().tryCode(() -> {
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
