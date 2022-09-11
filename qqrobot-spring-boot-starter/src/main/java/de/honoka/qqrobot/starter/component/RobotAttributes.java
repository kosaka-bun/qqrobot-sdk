package de.honoka.qqrobot.starter.component;

import de.honoka.qqrobot.starter.property.RobotBasicProperties;
import de.honoka.sdk.util.system.gui.ConsoleWindow;
import org.springframework.stereotype.Component;

/**
 * 系统赖以运行的参数与开关
 */
@Component
public class RobotAttributes {

    /**
     * 指示要固定回复的内容
     */
    public static final String
            //错误提示
            errMsg = "出现了异常，已将信息报告给开发者",
            wrongParameterMsg = "指定的参数有误",
            parameterNotEnoughMsg = "命令格式有误，提供的参数个数不足",
            noAuthorizationMsg = "你没有足够的权限执行此操作",
            offMsg = "处理消息功能处于关闭状态，可能在维护当中";

    /**
     * 指定可修改的回复内容
     */
    public String
            wrongCommandMsg = "指令有误，请检查输入\n请发送“%s菜单”查看指令",
            //菜单，由其他类加载生成后复赋值，覆盖掉默认内容
            menu = "菜单尚未初始化";

    /**
     * 可修改参数、开关等
     */
	//处理消息的总开关，由配置进行初始化
    public boolean isEnabled;

    public ConsoleWindow consoleWindow;

    public RobotAttributes(RobotBasicProperties basicProperties) {
        isEnabled = basicProperties.getDefaultEnabled();
    }
}
