package de.honoka.qqrobot.framework.api.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class RobotMessage<C> {

    private RobotMessageType type;

    private C content;

    private Map<String, Object> others = new HashMap<>();

    public RobotMessage(RobotMessageType type, C content) {
        this.type = type;
        this.content = content;
    }

    public static RobotMessage<String> text(String text) {
        return new RobotMessage<>(RobotMessageType.TEXT, text);
    }

    public static RobotMessage<Long> at(long qq) {
        return new RobotMessage<>(RobotMessageType.AT, qq);
    }

    public static RobotMessage<InputStream> image(InputStream image) {
        return new RobotMessage<>(RobotMessageType.IMAGE, image);
    }

    public static RobotMessage<InputStream> file(InputStream file) {
        return new RobotMessage<>(RobotMessageType.FILE, file);
    }
}
