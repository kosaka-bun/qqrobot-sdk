package de.honoka.qqrobot.framework.impl.tester;

import com.google.gson.JsonObject;
import de.honoka.qqrobot.framework.BaseFramework;
import de.honoka.qqrobot.framework.api.model.RobotMessage;
import de.honoka.qqrobot.framework.api.model.RobotMessageType;
import de.honoka.qqrobot.framework.api.model.RobotMultipartMessage;
import de.honoka.qqrobot.framework.config.TesterConfig;
import de.honoka.qqrobot.framework.impl.tester.config.TesterProperties;
import de.honoka.qqrobot.framework.impl.tester.model.TesterMessage;
import de.honoka.qqrobot.framework.impl.tester.model.TesterMessageType;
import de.honoka.qqrobot.framework.impl.tester.model.TesterRobotMessage;
import de.honoka.qqrobot.framework.impl.tester.server.TesterServer;
import de.honoka.qqrobot.framework.impl.tester.server.TesterServerConnection;
import de.honoka.qqrobot.starter.RobotBasicProperties;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Getter
@Component
public class TesterFramework extends BaseFramework<TesterRobotMessage> {

    @Resource
    private RobotBasicProperties basicProperties;

    @Resource
    private TesterProperties testerProperties;

    @Resource
    private TesterConfig testerConfig;

    @Resource
    private TesterServer testerServer;

    //key为图片数据流的hashCode，value为这个流对应的文件名
    private final Map<Integer, String> imageNameMap = new HashMap<>();

    @SneakyThrows
    @Override
    public synchronized void boot() {
        File imagePath = new File(testerProperties.getImagePath());
        if(imagePath.exists()) {
            FileUtils.forceDelete(imagePath);
        }
        frameworkCallback.onStartup();
        log.info("\nTester框架启动完成\n请访问 {} 进行测试", testerConfig.getTesterUrl());
    }

    @SneakyThrows
    @Override
    public synchronized void stop() {
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
    public TesterRobotMessage transform(Long group, long qq, RobotMultipartMessage message) {
        if(message == null || message.isEmpty()) return null;
        TesterRobotMessage testerRobotMessage = new TesterRobotMessage();
        for(RobotMessage<?> part : message.messageList) {
            switch(part.getType()) {
                case TEXT:
                    if(part.getContent() == null || part.getContent().equals("")) continue;
                    testerRobotMessage.add(TesterRobotMessage.PartType.TEXT, (String) part.getContent());
                    break;
                case AT:
                    if(group == null) break;
                    long atQq = (Long) part.getContent();
                    TesterRobotMessage.Part at = new TesterRobotMessage.Part(
                        TesterRobotMessage.PartType.AT, "@" + getNickOrCard(group, atQq) + " "
                    );
                    at.setExtras(new JsonObject());
                    at.getExtras().addProperty("qq", (long) part.getContent());
                    testerRobotMessage.add(at);
                    break;
                case IMAGE:
                    InputStream inputStream = (InputStream) part.getContent();
                    String name = imageNameMap.get(inputStream.hashCode());
                    if(name == null) {
                        name = UUID.randomUUID().toString();
                        imageNameMap.put(inputStream.hashCode(), name);
                        byte[] bytes = IOUtils.toByteArray(inputStream);
                        String path = Paths.get(testerProperties.getImagePath(), name + ".png").toString();
                        FileUtils.touch(new File(path));
                        try(OutputStream os = Files.newOutputStream(Paths.get(path))) {
                            os.write(bytes);
                        }
                    }
                    testerRobotMessage.add(TesterRobotMessage.PartType.IMAGE, name);
                    break;
                case FILE:
                    testerRobotMessage.add(TesterRobotMessage.PartType.TEXT, "【文件】");
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
                multipartMessage.add(RobotMessageType.AT, part.getExtras().get("qq").getAsLong());
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
            data.add("content", transform(null, qq, message).toJsonArray());
            connection.sendMessage(
                new TesterMessage(null)
                    .setType(TesterMessageType.PRIVATE_MESSAGE)
                    .setData(data)
            );
        }
    }
    
    @Override
    public void sendGroupMsg(long group, RobotMultipartMessage message) {
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
    public String getGroupName(long group) {
        return "Tester Group";
    }

    @Override
    public String getNickOrCard(long group, long qq) {
        for(TesterServerConnection connection : testerServer.getConnections()) {
            long qqOfConnection = connection.getData().get("qq").getAsLong();
            if(qqOfConnection == qq) {
                return connection.getData().get("name").getAsString();
            }
        }
        return null;
    }

    @Override
    public boolean isMuted(long group) {
        return false;
    }
}
