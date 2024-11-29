package de.honoka.qqrobot.framework;

import de.honoka.qqrobot.framework.api.Framework;
import de.honoka.qqrobot.framework.api.FrameworkCallback;
import jakarta.annotation.Resource;

public abstract class BaseFramework<M> extends Framework<M> {
    
    @Resource
    @Override
    protected void setFrameworkCallback(FrameworkCallback frameworkCallback) {
        super.setFrameworkCallback(frameworkCallback);
    }
}
