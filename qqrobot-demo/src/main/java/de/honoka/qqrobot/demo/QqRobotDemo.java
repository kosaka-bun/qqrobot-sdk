package de.honoka.qqrobot.demo;

import de.honoka.qqrobot.starter.component.RobotConsoleWindow;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QqRobotDemo {

    public static void main(String[] args) {
        RobotConsoleWindow demo = new RobotConsoleWindow("QQ Robot Demo",
                1.25, QqRobotDemo.class);
        demo.setOnExit(context -> {
            context.getBean(DemoSystemService.class).exit();
        }).setShowOnCreate(false).create();
    }
}
