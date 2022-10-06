package de.honoka.qqrobot.starter.component;

import de.honoka.qqrobot.framework.Framework;
import de.honoka.qqrobot.starter.RobotBasicProperties;
import de.honoka.sdk.util.system.gui.ConsoleWindow;
import de.honoka.sdk.util.text.TextUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.net.URL;
import java.util.Date;
import java.util.function.Consumer;

@Getter
public class RobotConsoleWindow {

    @Getter
    private static ConsoleWindow console;

    private final String name;

    @Setter
    private URL iconPath;

    private final double screenZoomScale;

    @Setter
    private Consumer<ApplicationContext> onExit = context -> {};

    private ApplicationContext context;

    private final Class<?> springBootMainClass;

    @Setter
    private String[] applicationArgs = new String[0];

    @Setter
    private boolean showOnCreate = true;

    public RobotConsoleWindow(String name, double screenZoomScale,
                              Class<?> springBootMainClass) {
        this.name = name;
        this.screenZoomScale = screenZoomScale;
        this.springBootMainClass = springBootMainClass;
    }

    public void create() {
        //创建并显示窗口
        console = new ConsoleWindow(name, iconPath, () -> {
            onExit.accept(context);
        });
        console.setAutoScroll(true);
        console.setScreenZoomScale(screenZoomScale);
        if(showOnCreate) {
            console.show();
        }
        //启动应用
        SpringApplication app = new SpringApplication(springBootMainClass);
        context = app.run(applicationArgs);
        //添加托盘图标菜单项
        Framework<?> framework = context.getBean(Framework.class);
        RobotBasicProperties basicProperties = context.getBean(
                RobotBasicProperties.class);
        console.addTrayIconMenuItem("重新登录", true,
                framework::reboot);
        console.addTrayIconMenuItem("发送测试消息",
                false, () -> {
            String time = TextUtils.getSimpleDateFormat().format(new Date());
            framework.sendGroupMsg(basicProperties.getDevelopingGroup(),
                    time + "\n测试消息");
        });
    }
}
