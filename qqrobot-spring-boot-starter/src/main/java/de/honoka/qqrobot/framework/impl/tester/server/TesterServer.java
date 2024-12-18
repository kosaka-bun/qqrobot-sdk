package de.honoka.qqrobot.framework.impl.tester.server;

import de.honoka.qqrobot.framework.api.Framework;
import de.honoka.qqrobot.framework.impl.tester.config.TesterProperties;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Getter
@Component
public class TesterServer {

    private List<TesterServerConnection> connections;

    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

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
