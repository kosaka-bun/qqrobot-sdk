package de.honoka.qqrobot.framework.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Iterator;

@Accessors(chain = true)
public class RobotMultipartMessage implements Cloneable {

    @Getter
    @Setter
    public ArrayList<RobotMessage<?>> messageList = new ArrayList<>();

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
    @SneakyThrows
    @Override
    public Object clone() {
        RobotMultipartMessage clone = (RobotMultipartMessage) super.clone();
        ArrayList<RobotMessage<?>> listCopy = (ArrayList<RobotMessage<?>>)
                messageList.clone();
        return clone.setMessageList(listCopy);
    }
}
