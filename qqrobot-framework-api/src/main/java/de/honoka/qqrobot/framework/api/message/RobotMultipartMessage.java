package de.honoka.qqrobot.framework.api.message;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Setter
@Getter
@Accessors(chain = true)
public class RobotMultipartMessage {

    public List<RobotMessage<?>> messageList = new ArrayList<>();

    public RobotMultipartMessage() {}

    public static RobotMultipartMessage of(RobotMessage<?> message) {
        RobotMultipartMessage multipartMessage = new RobotMultipartMessage();
        multipartMessage.messageList.add(message);
        return multipartMessage;
    }

    public static RobotMultipartMessage of(String text) {
        return of(RobotMessage.text(text));
    }

    public RobotMultipartMessage add(RobotMessage<?> message) {
        messageList.add(message);
        return this;
    }
    
    public RobotMultipartMessage add(String text) {
        add(RobotMessage.text(text));
        return this;
    }

    public RobotMessage<?> getFirst() {
        return messageList.get(0);
    }

    /**
     * 移除不必要的空串部分
     */
    public void removeEmptyPart() {
        for(Iterator<RobotMessage<?>> iterator = messageList.iterator(); iterator.hasNext(); ) {
            RobotMessage<?> part = iterator.next();
            if(part.getContent() == null) {
                iterator.remove();
            } else if(part.getContent() instanceof String) {
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
            Object content = message.getContent();
            if(content instanceof String) {
                sb.append(message.getContent());
            } else if(content instanceof RobotMessageTypes.Image) {
                sb.append("【图片】");
            } else if(content instanceof RobotMessageTypes.At) {
                sb.append("@").append(message.getContent()).append(" ");
            } else if(content instanceof RobotMessageTypes.File) {
                sb.append("【文件】");
            } else {
                sb.append("【未知消息内容部分】");
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return contentToString();
    }
}
