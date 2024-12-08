package de.honoka.qqrobot.starter.component;

import de.honoka.qqrobot.framework.api.Framework;
import de.honoka.qqrobot.starter.RobotBasicProperties;
import de.honoka.sdk.util.code.ThrowsRunnable;
import de.honoka.sdk.util.gui.ConsoleWindow;
import de.honoka.sdk.util.text.TextUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.net.URL;
import java.util.Date;
import java.util.function.Consumer;

@Getter
@Setter
@Accessors(chain = true)
public class RobotConsoleWindow {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static ConsoleWindow consoleWindow;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static RobotConsole console;

    private String name;

    private URL iconPath;

    private double screenZoomScale;

    private Class<?> springBootMainClass;

    private String[] applicationArgs = new String[0];

    private Consumer<ApplicationContext> onExit = context -> {};

    private ThrowsRunnable beforeRunApplication = () -> {};

    private boolean showOnCreate = true;

    @Setter(AccessLevel.NONE)
    private ApplicationContext context;

    /**
     * 强制不使用GUI窗口
     */
    private boolean forceNoGui = false;

    private RobotConsoleWindow() {}

    public static RobotConsoleWindow of(String name, Class<?> springBootMainClass) {
        RobotConsoleWindow window = new RobotConsoleWindow();
        window.name = name;
        window.springBootMainClass = springBootMainClass;
        return window;
    }

    public static RobotConsoleWindow of(String name, double screenZoomScale, Class<?> springBootMainClass) {
        RobotConsoleWindow window = new RobotConsoleWindow();
        window.name = name;
        window.screenZoomScale = screenZoomScale;
        window.springBootMainClass = springBootMainClass;
        return window;
    }

    private void startApplication() {
        beforeRunApplication.run();
        SpringApplication app = new SpringApplication(springBootMainClass);
        context = app.run(applicationArgs);
    }

    public void create() {
        //优先使用VM选项中的配置，其次使用代码中指定的配置
        String forceNoGuiInVmOptions = System.getProperty("honoka.gui.force-no-gui");
        boolean forceNoGui = forceNoGuiInVmOptions == null ? this.forceNoGui : Boolean.parseBoolean(forceNoGuiInVmOptions);
        if(forceNoGui) {
            console = new RobotConsole();
            startApplication();
            return;
        }
        //创建并显示窗口
        try {
            consoleWindow = ConsoleWindow.Builder.of(name).setOnExit(() -> {
                onExit.accept(context);
            }).setScreenZoomScale(screenZoomScale).build();
            consoleWindow.setAutoScroll(true);
        } catch(Throwable t) {
            //若不能加载窗口，则直接以控制台方式运行
            console = new RobotConsole();
            startApplication();
            return;
        }
        //启动应用
        startApplication();
        //添加托盘图标菜单项
        Framework<?> framework = context.getBean(Framework.class);
        RobotBasicProperties basicProperties = context.getBean(RobotBasicProperties.class);
        consoleWindow.addTrayIconMenuItem("重新登录", true, framework::reboot);
        consoleWindow.addTrayIconMenuItem("发送测试消息", false, () -> {
            String time = TextUtils.getSimpleDateFormat().format(new Date());
            framework.sendGroupMsg(basicProperties.getDevelopingGroup(), time + "\n测试消息");
        });
    }

    public static String getConsoleText() {
        if(consoleWindow != null) {
            return consoleWindow.getText();
        } else if(console != null) {
            return console.getText();
        }
        return null;
    }
}
