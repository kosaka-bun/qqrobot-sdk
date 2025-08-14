package de.honoka.qqrobot.starter.component.logger.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Accessors(chain = true)
@Data
public class ExceptionRecord implements Serializable {

    private Integer id;

    private Date datetime;

    private String exceptionText;
}
