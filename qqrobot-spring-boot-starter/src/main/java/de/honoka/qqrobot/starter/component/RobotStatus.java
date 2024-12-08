package de.honoka.qqrobot.starter.component;

import de.honoka.qqrobot.starter.config.property.RobotBasicProperties;
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

    public RobotStatus(RobotBasicProperties basicProperties) {
        enabled = basicProperties.isDefaultEnabled();
    }
}
