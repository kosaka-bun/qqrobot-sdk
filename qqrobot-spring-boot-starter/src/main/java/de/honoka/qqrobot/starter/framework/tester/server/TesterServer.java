package de.honoka.qqrobot.starter.framework.tester.server;

import de.honoka.qqrobot.framework.Framework;
import de.honoka.qqrobot.starter.framework.tester.config.TesterProperties;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
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

    @Lazy
    @Resource
    private Framework<?> framework;

    @Resource
    private TesterProperties testerProperties;

    public TesterServer() {
        resetConnections();
    }

    public void resetConnections() {
        connections = Collections.synchronizedList(new LinkedList<>());
    }
}
