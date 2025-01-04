package de.honoka.qqrobot.framework;

import de.honoka.qqrobot.framework.api.Framework;
import de.honoka.qqrobot.framework.api.FrameworkCallback;
import de.honoka.qqrobot.framework.api.model.RobotMultipartMessage;
import de.honoka.qqrobot.starter.config.RobotBasicProperties;
import jakarta.annotation.Resource;
import lombok.Getter;

import java.util.Objects;

public abstract class BaseFramework<M> implements Framework<M> {
    
    @Resource
    protected RobotBasicProperties basicProperties;
    
    @Getter
    @Resource
    protected FrameworkCallback frameworkCallback;
    
    public void sendMsgToAdmin(RobotMultipartMessage message) {
        long qq = Objects.requireNonNull(basicProperties.getAdminQq());
        sendPrivateMsg(qq, message);
    }
    
    public void sendMsgToDevelopingGroup(RobotMultipartMessage message) {
        long group = Objects.requireNonNull(basicProperties.getDevelopingGroup());
        sendGroupMsg(group, message);
    }
}
