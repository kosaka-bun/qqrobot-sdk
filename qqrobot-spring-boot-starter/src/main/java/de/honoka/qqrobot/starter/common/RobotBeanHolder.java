package de.honoka.qqrobot.starter.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

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
