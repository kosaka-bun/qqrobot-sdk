package de.honoka.qqrobot.spring.boot.starter.component;

import de.honoka.qqrobot.framework.FrameworkCallback;
import de.honoka.qqrobot.framework.model.RobotMultipartMessage;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class DefaultFrameworkCallback implements FrameworkCallback {

    protected MessageExecutor messageExecutor;

    protected RobotBeanHolder beanHolder;

    private final ThreadPoolExecutor threadPoolExecutor =
            (ThreadPoolExecutor) Executors.newCachedThreadPool();

    public DefaultFrameworkCallback(MessageExecutor messageExecutor,
                                    RobotBeanHolder beanHolder) {
        this.messageExecutor = messageExecutor;
        this.beanHolder = beanHolder;
    }

    /**
     * 收到私聊消息
     */
    @Override
    public void onPrivateMsg(long qq, RobotMultipartMessage msg) {
        threadPoolExecutor.submit(() -> {
            //回复信息
            RobotMultipartMessage reply = messageExecutor.executeMsg(
                    null, qq, msg);
            if(reply != null) {
                beanHolder.getFramework().reply(null, qq, reply);
            }
        });
    }

    /**
     * 收到群消息
     */
    @Override
    public void onGroupMsg(Long group, long qq, RobotMultipartMessage msg) {
        //若机器人被禁言，则不响应此消息
        if(beanHolder.getFramework().isMuted(group)) return;
        threadPoolExecutor.submit(() -> {
            //回复信息
            RobotMultipartMessage reply = messageExecutor.executeMsg(group, qq, msg);
            if(reply != null) {
                beanHolder.getFramework().reply(group, qq, reply);
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
