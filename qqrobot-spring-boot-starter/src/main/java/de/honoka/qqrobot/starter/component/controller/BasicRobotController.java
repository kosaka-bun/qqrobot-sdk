package de.honoka.qqrobot.starter.component.controller;

import de.honoka.qqrobot.framework.api.model.RobotMessage;
import de.honoka.qqrobot.starter.common.annotation.Command;
import de.honoka.qqrobot.starter.common.annotation.RobotController;
import de.honoka.qqrobot.starter.component.MessageExecutor;
import de.honoka.sdk.util.various.ImageUtils;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;

import java.io.InputStream;

@SuppressWarnings("unused")
@RobotController
public class BasicRobotController {

    @Lazy
    @Resource
    private MessageExecutor messageExecutor;

    @Command("菜单")
    public RobotMessage<InputStream> menu() {
        return RobotMessage.image(ImageUtils.textToImageByLength(
                messageExecutor.getMenu(), 30));
    }
}
