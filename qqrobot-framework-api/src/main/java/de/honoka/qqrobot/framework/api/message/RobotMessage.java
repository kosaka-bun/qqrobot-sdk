package de.honoka.qqrobot.framework.api.message;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Accessors(chain = true)
@Data
public class RobotMessage<C> {

    private C content;

    private Map<String, Object> others = new HashMap<>();

    private RobotMessage(C content) {
        this.content = content;
    }

    public static RobotMessage<String> text(String text) {
        return new RobotMessage<>(text);
    }

    public static RobotMessage<RobotMessageTypes.At> at(long qq) {
        return new RobotMessage<>(new RobotMessageTypes.At(qq));
    }

    public static RobotMessage<RobotMessageTypes.Image> image(InputStream image) {
        return new RobotMessage<>(new RobotMessageTypes.Image(image));
    }

    public static RobotMessage<RobotMessageTypes.File> file(InputStream file) {
        return new RobotMessage<>(new RobotMessageTypes.File(file));
    }
}
