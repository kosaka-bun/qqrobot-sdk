package de.honoka.qqrobot.framework;

import de.honoka.qqrobot.framework.api.message.RobotMultipartMessage;

public abstract class TypedRobotFramework<M> extends ExtendedRobotFramework {
    
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
}
