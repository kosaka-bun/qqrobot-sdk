package de.honoka.qqrobot.starter.component;

import de.honoka.qqrobot.framework.api.Framework;
import de.honoka.qqrobot.framework.api.FrameworkCallback;
import de.honoka.qqrobot.framework.api.model.CallerInfo;
import de.honoka.qqrobot.framework.api.model.RobotMultipartMessage;
import de.honoka.qqrobot.starter.common.ConditionalBeans;
import de.honoka.qqrobot.starter.common.annotation.ConditionalComponent;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;

@ConditionalComponent(ConditionalBeans.class)
public class DefaultFrameworkCallback extends FrameworkCallback {

    @Lazy
    @Resource
    private MessageExecutor messageExecutor;

    @Lazy
    @Resource
    private Framework<?> framework;

    /**
     * 收到私聊消息
     */
    @Override
    protected void onPrivateMsg(RobotMultipartMessage msg) {
        CallerInfo callerInfo = callerInfoHolder.get();
        //回复信息
        RobotMultipartMessage reply = messageExecutor.executeMsg(null, callerInfo.getQq(), msg);
        if(reply != null) {
            reply.removeEmptyPart();
            if(reply.isEmpty()) return;
            if(callerInfo.getGroup() != null) {
                framework.sendTempPrivateMsg(callerInfo.getGroup(), callerInfo.getQq(), reply);
            } else {
                framework.sendPrivateMsg(callerInfo.getQq(), reply);
            }
        }
    }
    
    /**
     * 收到群消息
     */
    @Override
    protected void onGroupMsg(RobotMultipartMessage msg) {
        CallerInfo callerInfo = callerInfoHolder.get();
        //若机器人被禁言，则不响应此消息
        if(framework.isMuted(callerInfo.getGroup())) return;
        //回复信息
        RobotMultipartMessage reply = messageExecutor.executeMsg(
            callerInfo.getGroup(), callerInfo.getQq(), msg
        );
        if(reply != null) {
            reply.removeEmptyPart();
            if(reply.isEmpty()) return;
            framework.reply(callerInfo.getGroup(), callerInfo.getQq(), reply);
        }
    }

    /**
     * 机器人启动
     */
    @Override
    public void onStartup() {}

    /**
     * 机器人关闭
     */
    @Override
    public void onShutdown() {}
}
