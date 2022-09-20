package de.honoka.qqrobot.starter.component.logger.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class UsageLog implements Serializable {

    private Integer id;

    /**
     * 时间戳
     */
    private Date datetime;

    /**
     * QQ号码
     */
    private Long qq;

    /**
     * 群名，群名片或昵称，执行的操作，处理的信息，回复的信息
     */
    private String groupName;

    private String username;

    private String msg;

    private String reply;
}
