package de.honoka.qqrobot.starter.component;

import de.honoka.qqrobot.starter.RobotBasicProperties;
import de.honoka.sdk.util.system.gui.ConsoleWindow;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * 系统赖以运行的参数与开关
 */
@Setter
@Getter
@Component
public class RobotStatus {

    /**
     * 处理消息的总开关，由配置进行初始化
     */
    private boolean enabled;

    private ConsoleWindow consoleWindow;

    public RobotStatus(RobotBasicProperties basicProperties) {
        enabled = basicProperties.getDefaultEnabled();
    }
}
