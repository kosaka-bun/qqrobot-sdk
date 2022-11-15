package de.honoka.qqrobot.framework.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Accessors(chain = true)
public class RobotMultipartMessage {

    @Getter
    @Setter
    public List<RobotMessage<?>> messageList = new ArrayList<>();

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

    /**
     * 移除不必要的空串部分
     */
    public void removeEmptyPart() {
        for(Iterator<RobotMessage<?>> iterator = messageList.iterator();
            iterator.hasNext(); ) {
            RobotMessage<?> part = iterator.next();
            if(part.getContent() == null) {
                iterator.remove();
            } else if(part.getType().equals(RobotMessageType.TEXT)) {
                if(part.getContent().equals("")) {
                    iterator.remove();
                }
            }
        }
    }

    public boolean isEmpty() {
        return messageList.isEmpty();
    }

    public String contentToString() {
        StringBuilder sb = new StringBuilder();
        for(RobotMessage<?> message : messageList) {
            switch(message.getType()) {
                case TEXT:
                    sb.append(message.getContent());
                    break;
                case IMAGE:
                    sb.append("【图片】");
                    break;
                case AT:
                    sb.append("@").append(message.getContent())
                            .append(" ");
                    break;
                case FILE:
                    sb.append("【文件】");
                    break;
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return contentToString();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object clone() throws CloneNotSupportedException {
        RobotMultipartMessage clone = (RobotMultipartMessage) super.clone();
        Object listCopy = ((ArrayList<?>) messageList).clone();
        return clone.setMessageList((List<RobotMessage<?>>) listCopy);
    }
}
