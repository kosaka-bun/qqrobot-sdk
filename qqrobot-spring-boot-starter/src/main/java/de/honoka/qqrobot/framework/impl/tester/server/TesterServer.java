package de.honoka.qqrobot.framework.impl.tester.server;

import de.honoka.qqrobot.framework.config.TesterProperties;
import de.honoka.qqrobot.framework.impl.tester.TesterFramework;
import de.honoka.sdk.util.concurrent.ThreadPoolUtils;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Getter
@Component
public class TesterServer {

    private List<TesterServerConnection> connections;

    private final ThreadPoolExecutor executor = ThreadPoolUtils.newEagerThreadPool(
        1, 2, 10, TimeUnit.SECONDS
    );

    @Lazy
    @Resource
    private TesterFramework framework;

    @Resource
    private TesterProperties testerProperties;

    public TesterServer() {
        resetConnections();
    }

    public void resetConnections() {
        connections = Collections.synchronizedList(new LinkedList<>());
    }
}
