package de.honoka.qqrobot.starter.component.logger;

import cn.hutool.core.exceptions.ExceptionUtil;
import de.honoka.qqrobot.framework.api.RobotFramework;
import de.honoka.qqrobot.starter.component.logger.dao.ExceptionRecordDao;
import de.honoka.qqrobot.starter.component.logger.dao.UsageLogDao;
import de.honoka.qqrobot.starter.component.logger.entity.ExceptionRecord;
import de.honoka.qqrobot.starter.component.logger.entity.UsageLog;
import de.honoka.qqrobot.starter.config.ConditionalBeansConfig;
import de.honoka.sdk.spring.starter.core.context.ConditionalComponent;
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
    private RobotFramework framework;
    
    @Resource
    private ExceptionRecordDao exceptionRecordDao;
    
    @Override
    public void logException(Throwable t) {
        String str = ExceptionUtil.stacktraceToString(t, -1);
        new Retrier().tryCode(() -> exceptionRecordDao.insert(
            new ExceptionRecord()
                .setDatetime(new Date())
                .setExceptionText(str)
        ));
    }
    
    @Override
    public void logMsgExecution(Long group, long qq, String msg, String reply) {
        new Retrier().tryCode(() -> usageLogDao.insert(
            new UsageLog()
                .setGroupName(framework.getGroupName(group))
                .setQq(qq).setDatetime(new Date())
                .setUsername(framework.getNickOrCard(group, qq))
                .setMsg(msg)
                .setReply(reply)
        ));
    }
}
