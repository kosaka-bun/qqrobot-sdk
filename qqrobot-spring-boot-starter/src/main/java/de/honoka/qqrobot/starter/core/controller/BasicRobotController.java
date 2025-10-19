package de.honoka.qqrobot.starter.core.controller;

import de.honoka.qqrobot.framework.api.message.RobotMessage;
import de.honoka.qqrobot.starter.common.annotation.Command;
import de.honoka.qqrobot.starter.common.annotation.RobotController;
import de.honoka.qqrobot.starter.core.MessageExecutor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;

@SuppressWarnings("unused")
@RobotController
public class BasicRobotController {

    @Lazy
    @Resource
    private MessageExecutor messageExecutor;

    @Command("菜单")
    public RobotMessage<String> menu() {
        return RobotMessage.text(messageExecutor.getMenu());
    }
}
