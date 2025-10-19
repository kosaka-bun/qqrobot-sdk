package de.honoka.qqrobot.starter.framework;

import de.honoka.qqrobot.framework.api.RobotFramework;
import de.honoka.qqrobot.framework.api.RobotFrameworkCallback;
import de.honoka.qqrobot.framework.api.message.RobotMultipartMessage;
import de.honoka.qqrobot.starter.config.RobotBasicProperties;
import jakarta.annotation.Resource;
import lombok.Getter;

import java.util.Objects;

public abstract class ExtendedRobotFramework implements RobotFramework {
    
    @Resource
    protected RobotBasicProperties basicProperties;
    
    @Getter
    @Resource
    protected RobotFrameworkCallback frameworkCallback;
    
    public void sendMsgToAdmin(RobotMultipartMessage message) {
        long qq = Objects.requireNonNull(basicProperties.getAdminQq());
        sendPrivateMsg(qq, message);
    }
    
    public void sendMsgToDevelopingGroup(RobotMultipartMessage message) {
        long group = Objects.requireNonNull(basicProperties.getDevelopingGroup());
        sendGroupMsg(group, message);
    }
}
