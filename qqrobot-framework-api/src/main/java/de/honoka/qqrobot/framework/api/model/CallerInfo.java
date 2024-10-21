package de.honoka.qqrobot.framework.api.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 命令调用者信息
 */
@Data
@Accessors(chain = true)
public class CallerInfo {
    
    //为null时一定为私聊消息，不为null时可能为群聊消息或群临时会话消息
    private Long group;
    
    private long qq;
    
    /**
     * 是否是群聊消息
     */
    private boolean isGroupMsg = false;
}
