package de.honoka.qqrobot.demo;

import org.springframework.stereotype.Component;

@Component
public class DemoSystemService {

    public void exit() {
        System.out.println("系统退出");
    }
}
