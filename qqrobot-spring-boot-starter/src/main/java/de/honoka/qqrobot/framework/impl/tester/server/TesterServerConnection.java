package de.honoka.qqrobot.framework.impl.tester.server;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.honoka.qqrobot.framework.api.Framework;
import de.honoka.qqrobot.framework.impl.tester.config.TesterProperties;
import de.honoka.qqrobot.framework.impl.tester.model.TesterMessage;
import de.honoka.qqrobot.framework.impl.tester.model.TesterMessageType;
import de.honoka.qqrobot.framework.impl.tester.model.TesterRobotMessage;
import de.honoka.qqrobot.starter.common.RobotBeanHolder;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Getter;

import java.util.Objects;

@ServerEndpoint(TesterProperties.WEB_PREFIX + "/server")
@Getter
public class TesterServerConnection {

    private static TesterServer testerServer;

    private Session session;

    private JsonObject data;

    @SuppressWarnings("unused")
    public TesterServerConnection() {}

    public TesterServerConnection(TesterServer server) {
        testerServer = server;
    }

    public void sendMessage(TesterMessage message) {
        try {
            this.session.getBasicRemote().sendText(RobotBeanHolder.gson.toJson(message));
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(String messageStr) {
        TesterMessage message = RobotBeanHolder.gson.fromJson(messageStr, TesterMessage.class);
        switch(message.getType()) {
            case TesterMessageType.LOGIN:
                sendMessage(onLogin(message));
                break;
            case TesterMessageType.QUERY_ONLINE:
                sendMessage(queryOnline(message));
                break;
            case TesterMessageType.GROUP_MESSAGE:
                onGroupMessage(message);
                break;
            case TesterMessageType.PRIVATE_MESSAGE:
                onPrivateMessage(message);
                break;
        }
    }

    @OnClose
    public void onClose() {
        testerServer.getConnections().remove(this);
        for(TesterServerConnection connection : testerServer.getConnections()) {
            connection.sendMessage(
                new TesterMessage(null)
                    .setType(TesterMessageType.UESR_LOGOUT)
                    .setData(data)
            );
        }
    }

    private TesterMessage onLogin(TesterMessage message) {
        TesterMessage res = new TesterMessage(message.getId());
        res.setType(TesterMessageType.LOGIN);
        //提取并检查消息中的成员
        JsonObject msgData = message.getData();
        long qq = msgData.get("qq").getAsLong();
        String username = msgData.get("name").getAsString();
        JsonObject resData = res.getData();
        if(StrUtil.equalsIgnoreCase(username, "robot")) {
            resData.addProperty("status", false);
            resData.addProperty("message", "不能使用Robot作为用户名");
            return res;
        }
        if(!ObjectUtil.isAllNotEmpty(qq, username) || username.isEmpty()) {
            resData.addProperty("status", false);
            resData.addProperty("message", "账号和用户名不能为空");
            return res;
        }
        //遍历现有连接，检查是否已登录
        for(TesterServerConnection connection : testerServer.getConnections()) {
            if(connection.data.get("qq").getAsLong() == qq) {
                resData.addProperty("status", false);
                resData.addProperty("message", "该账号已登录");
                return res;
            }
            if(Objects.equals(connection.data.get("name").getAsString(), username)) {
                resData.addProperty("status", false);
                resData.addProperty("message", "该用户名已存在");
                return res;
            }
        }
        data = msgData.deepCopy();
        testerServer.getConnections().add(this);
        //登录成功，通知所有在线用户
        testerServer.getExecutor().execute(() -> {
            for(TesterServerConnection connection : testerServer.getConnections()) {
                if(connection.data.get("name").getAsString().equals(username))
                    continue;
                JsonObject notifyMsgData = new JsonObject();
                notifyMsgData.addProperty("name", username);
                notifyMsgData.addProperty("qq", qq);
                notifyMsgData.add("data", data);
                connection.sendMessage(
                    new TesterMessage(null)
                        .setType(TesterMessageType.NEW_USER_LOGIN)
                        .setData(notifyMsgData)
                );
            }
        });
        res.getData().addProperty("status", true);
        return res;
    }

    private TesterMessage queryOnline(TesterMessage message) {
        TesterMessage res = new TesterMessage(message.getId());
        JsonArray messageData = new JsonArray();
        for(TesterServerConnection connection : testerServer.getConnections()) {
            JsonObject item = new JsonObject();
            item.addProperty("name", connection.data.get("name").getAsString());
            item.add("data", connection.data);
            messageData.add(item);
        }
        JsonObject resData = new JsonObject();
        resData.add("online", messageData);
        res.setData(resData);
        return res;
    }

    @SuppressWarnings("unchecked")
    private void onGroupMessage(TesterMessage message) {
        //收到消息，发送回执
        JsonObject resData = new JsonObject();
        resData.addProperty("status", true);
        sendMessage(
            new TesterMessage(message.getId())
                .setType(TesterMessageType.GROUP_MESSAGE_RESPONSE)
                .setData(resData)
        );
        //提醒其他用户
        JsonArray content = message.getData().getAsJsonArray("content");
        testerServer.getExecutor().execute(() -> {
            for(TesterServerConnection connection : testerServer.getConnections()) {
                if(connection.data.get("name").getAsString().equals(data.get("name").getAsString()))
                    continue;
                JsonObject notifyMsgData = new JsonObject();
                notifyMsgData.addProperty("name", data.get("name").getAsString());
                notifyMsgData.add("content", content);
                connection.sendMessage(
                    new TesterMessage(null)
                        .setType(TesterMessageType.GROUP_MESSAGE)
                        .setData(notifyMsgData)
                );
            }
        });
        //处理消息
        Framework<TesterRobotMessage> framework = (Framework<TesterRobotMessage>) testerServer.getFramework();
        framework.getFrameworkCallback().onGroupMsg(
            testerServer.getTesterProperties().getGroupNumber(),
            data.get("qq").getAsLong(),
            framework.transform(TesterRobotMessage.of(content))
        );
    }

    @SuppressWarnings("unchecked")
    private void onPrivateMessage(TesterMessage message) {
        //收到消息，发送回执
        JsonObject resData = new JsonObject();
        resData.addProperty("status", true);
        sendMessage(
            new TesterMessage(message.getId())
                .setType(TesterMessageType.PRIVATE_MESSAGE_RESPONSE)
                .setData(resData)
        );
        //处理消息
        JsonArray content = message.getData().getAsJsonArray("content");
        Framework<TesterRobotMessage> framework = (Framework<TesterRobotMessage>) testerServer.getFramework();
        framework.getFrameworkCallback().onPrivateMsg(
            data.get("qq").getAsLong(),
            framework.transform(TesterRobotMessage.of(content))
        );
    }
}
