package de.honoka.qqrobot.spring.boot.starter.component.controller;

import de.honoka.qqrobot.spring.boot.starter.annotation.Command;
import de.honoka.qqrobot.spring.boot.starter.annotation.RobotController;
import de.honoka.qqrobot.spring.boot.starter.component.RobotAttributes;
import de.honoka.qqrobot.spring.boot.starter.component.util.RobotImageUtils;

import javax.annotation.Resource;

@SuppressWarnings("unused")
@RobotController
public class BasicRobotController {

    @Resource
    private RobotImageUtils robotImageUtils;

    @Resource
    private RobotAttributes robotAttributes;

    @Command("菜单")
    public String menu() {
        return robotImageUtils.textToImageByLength(robotAttributes.menu,
                25);
    }
}
