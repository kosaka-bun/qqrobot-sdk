package de.honoka.qqrobot.starter.component;

import de.honoka.qqrobot.framework.api.RobotFramework;
import de.honoka.qqrobot.framework.api.RobotFrameworkCallback;
import de.honoka.qqrobot.framework.api.model.RobotMultipartMessage;
import de.honoka.qqrobot.starter.common.annotation.ConditionalComponent;
import de.honoka.qqrobot.starter.config.ConditionalBeansConfig;
import de.honoka.qqrobot.starter.util.GlobalThreadPools;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;

@ConditionalComponent(ConditionalBeansConfig.class)
public class DefaultFrameworkCallback implements RobotFrameworkCallback {

    @Lazy
    @Resource
    private MessageExecutor messageExecutor;

    @Lazy
    @Resource
    private RobotFramework<?> framework;

    /**
     * 收到私聊消息
     */
    @Override
    public void onPrivateMsg(long qq, RobotMultipartMessage msg) {
        GlobalThreadPools.instantPool.submit(() -> {
            //回复信息
            RobotMultipartMessage reply = messageExecutor.executeMsg(null, qq, msg);
            if(reply != null) {
                reply.removeEmptyPart();
                if(reply.isEmpty()) return;
                framework.reply(null, qq, reply);
            }
        });
    }

    /**
     * 收到群消息
     */
    @Override
    public void onGroupMsg(long group, long qq, RobotMultipartMessage msg) {
        //若机器人被禁言，则不响应此消息
        if(framework.isMuted(group)) return;
        GlobalThreadPools.instantPool.submit(() -> {
            //回复信息
            RobotMultipartMessage reply = messageExecutor.executeMsg(group, qq, msg);
            if(reply != null) {
                reply.removeEmptyPart();
                if(reply.isEmpty()) return;
                framework.reply(group, qq, reply);
            }
        });
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
