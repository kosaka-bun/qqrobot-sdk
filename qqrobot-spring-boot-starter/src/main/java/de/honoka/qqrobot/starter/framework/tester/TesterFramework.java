package de.honoka.qqrobot.starter.framework.tester;

import com.google.gson.JsonObject;
import de.honoka.qqrobot.framework.Framework;
import de.honoka.qqrobot.framework.FrameworkCallback;
import de.honoka.qqrobot.framework.model.RobotMessage;
import de.honoka.qqrobot.framework.model.RobotMessageType;
import de.honoka.qqrobot.framework.model.RobotMultipartMessage;
import de.honoka.qqrobot.starter.framework.tester.config.TesterProperties;
import de.honoka.qqrobot.starter.framework.tester.model.TesterMessage;
import de.honoka.qqrobot.starter.framework.tester.model.TesterMessageType;
import de.honoka.qqrobot.starter.framework.tester.model.TesterRobotMessage;
import de.honoka.qqrobot.starter.framework.tester.server.TesterServer;
import de.honoka.qqrobot.starter.framework.tester.server.TesterServerConnection;
import de.honoka.qqrobot.starter.property.RobotBasicProperties;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import javax.annotation.Resource;
import java.io.InputStream;

@Getter
@ConditionalOnProperty(prefix = "honoka.qqrobot",
        name = "framework", havingValue = "tester")
@Component
public class TesterFramework extends Framework<TesterRobotMessage> {

    private final RobotBasicProperties basicProperties;

    private final TesterProperties testerProperties;

    @Resource
    private TesterServer testerServer;

    public TesterFramework(FrameworkCallback frameworkCallback,
                           RobotBasicProperties basicProperties,
                           TesterProperties testerProperties) {
        super(frameworkCallback);
        this.basicProperties = basicProperties;
        this.testerProperties = testerProperties;
    }

    public static final long GROUP_NUMBER = 10000;

    @Override
    public void boot() {
        frameworkCallback.onStartup();
    }

    @SneakyThrows
    @Override
    public void stop() {
        for(TesterServerConnection connection : testerServer.getConnections()) {
            connection.getSession().close();
        }
        testerServer.resetConnections();
        frameworkCallback.onShutdown();
    }

    @Override
    public void reboot() {
        stop();
        boot();
    }

    @SneakyThrows
    @Override
    public TesterRobotMessage transform(Long group, long qq,
                                        RobotMultipartMessage message) {
        if(message == null || message.isEmpty()) return null;
        TesterRobotMessage testerRobotMessage = new TesterRobotMessage();
        for(RobotMessage<?> part : message.messageList) {
            switch(part.getType()) {
                case TEXT:
                    if(part.getContent() == null || part.getContent().equals(""))
                        continue;
                    testerRobotMessage.add(TesterRobotMessage.PartType.TEXT,
                            (String) part.getContent());
                    break;
                case AT:
                    if(group == null) break;
                    TesterRobotMessage.Part at = new TesterRobotMessage.Part(
                            TesterRobotMessage.PartType.AT,
                            "@" + getNickOrCard(group, qq) + " "
                    );
                    at.setExtras(new JsonObject());
                    at.getExtras().addProperty("qq",
                            (long) part.getContent());
                    testerRobotMessage.add(at);
                    break;
                case IMAGE:
                    InputStream inputStream = (InputStream) part.getContent();
                    byte[] bytes = IOUtils.toByteArray(inputStream);
                    String base64 = Base64Utils.encodeToString(bytes);
                    base64 = "data:image/png;base64," + base64;
                    testerRobotMessage.add(TesterRobotMessage.PartType.IMAGE, base64);
                    break;
                case FILE:
                    break;
            }
        }
        return testerRobotMessage;
    }

    @Override
    public RobotMultipartMessage transform(TesterRobotMessage message) {
        RobotMultipartMessage multipartMessage = new RobotMultipartMessage();
        for(TesterRobotMessage.Part part : message.getParts()) {
            if(part.getType().equals(TesterRobotMessage.PartType.AT)) {
                multipartMessage.add(RobotMessageType.AT, part.getExtras()
                        .get("qq").getAsLong());
            } else {
                multipartMessage.add(RobotMessageType.TEXT, part.getContent());
            }
        }
        return multipartMessage;
    }

    @Override
    public void sendPrivateMsg(long qq, RobotMultipartMessage message) {
        for(TesterServerConnection connection : testerServer.getConnections()) {
            long qqOfConnection = connection.getData().get("qq").getAsLong();
            if(qqOfConnection != qq) continue;
            JsonObject data = new JsonObject();
            data.addProperty("name", "Robot");
            data.add("content", transform(null, qq, message)
                    .toJsonArray());
            connection.sendMessage(new TesterMessage(null)
                    .setType(TesterMessageType.PRIVATE_MESSAGE)
                    .setData(data)
            );
        }
    }

    @Override
    public void sendGroupMsg(Long group, RobotMultipartMessage message) {
        for(TesterServerConnection connection : testerServer.getConnections()) {
            JsonObject data = new JsonObject();
            data.addProperty("name", "Robot");
            data.add("content", transform(group, 0, message)
                    .toJsonArray());
            connection.sendMessage(new TesterMessage(null)
                    .setType(TesterMessageType.GROUP_MESSAGE)
                    .setData(data)
            );
        }
    }

    @Override
    public String getGroupName(Long group) {
        return "Tester Group";
    }

    @Override
    public String getNickOrCard(Long group, long qq) {
        for(TesterServerConnection connection : testerServer.getConnections()) {
            long qqOfConnection = connection.getData().get("qq").getAsLong();
            if(qqOfConnection == qq) {
                return connection.getData().get("name").getAsString();
            }
        }
        return null;
    }

    @Override
    public boolean isMuted(Long group) {
        return false;
    }
}
