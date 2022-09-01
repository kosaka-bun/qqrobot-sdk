package de.honoka.qqrobot.framework.model;

import java.util.ArrayList;
import java.util.List;

public class RobotMultipartMessage {

    public final List<RobotMessage<?>> messageList = new ArrayList<>();

    public RobotMultipartMessage() {}

    public static RobotMultipartMessage of(RobotMessage<?> message) {
        RobotMultipartMessage multipartMessage = new RobotMultipartMessage();
        multipartMessage.messageList.add(message);
        return multipartMessage;
    }

    public static <C> RobotMultipartMessage of(RobotMessageType type, C content) {
        return of(new RobotMessage<>(type, content));
    }

    public static RobotMultipartMessage of(String text) {
        return of(new RobotMessage<>(RobotMessageType.TEXT, text));
    }

    public <C> RobotMultipartMessage add(RobotMessageType type, C content) {
        messageList.add(new RobotMessage<>(type, content));
        return this;
    }

    public <C> RobotMultipartMessage add(RobotMessage<C> message) {
        messageList.add(message);
        return this;
    }

    public RobotMessage<?> getFirst() {
        return messageList.get(0);
    }
}
