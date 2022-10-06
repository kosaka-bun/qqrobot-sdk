package de.honoka.qqrobot.starter.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 解决循环依赖
 */
@Getter
@Component
public class RobotBeanHolder {

    @Resource
    private ApplicationContext applicationContext;

    public static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
}
