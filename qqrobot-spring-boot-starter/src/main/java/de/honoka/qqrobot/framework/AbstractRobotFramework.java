package de.honoka.qqrobot.framework;

import de.honoka.qqrobot.framework.api.RobotFramework;
import de.honoka.qqrobot.framework.api.RobotFrameworkCallback;
import de.honoka.qqrobot.framework.api.model.RobotMultipartMessage;
import de.honoka.qqrobot.starter.config.RobotBasicProperties;
import jakarta.annotation.Resource;
import lombok.Getter;

import java.util.Objects;

public abstract class AbstractRobotFramework<M> implements RobotFramework {
    
    @Resource
    protected RobotBasicProperties basicProperties;
    
    @Getter
    @Resource
    protected RobotFrameworkCallback frameworkCallback;
    
    public abstract M typedTransform(Long group, long qq, RobotMultipartMessage message);
    
    public abstract RobotMultipartMessage typedTransform(M message);
    
    @Override
    public final Object transform(Long group, long qq, RobotMultipartMessage message) {
        return typedTransform(group, qq, message);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public final RobotMultipartMessage transform(Object message) {
        return typedTransform((M) message);
    }
    
    public void sendMsgToAdmin(RobotMultipartMessage message) {
        long qq = Objects.requireNonNull(basicProperties.getAdminQq());
        sendPrivateMsg(qq, message);
    }
    
    public void sendMsgToDevelopingGroup(RobotMultipartMessage message) {
        long group = Objects.requireNonNull(basicProperties.getDevelopingGroup());
        sendGroupMsg(group, message);
    }
}
