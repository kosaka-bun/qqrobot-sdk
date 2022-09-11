package de.honoka.qqrobot.starter.framework.tester.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.honoka.qqrobot.starter.component.RobotBeanHolder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Getter
@Component
public class TesterServer {

    private List<TesterServerConnection> connections;

    private final ThreadPoolExecutor executor = (ThreadPoolExecutor)
            Executors.newCachedThreadPool();

    public static Gson gson = new GsonBuilder()
            .serializeNulls()
            .create();

    @Resource
    private RobotBeanHolder robotBeanHolder;

    public TesterServer() {
        resetConnections();
    }

    public void resetConnections() {
        connections = Collections.synchronizedList(new LinkedList<>());
    }
}
