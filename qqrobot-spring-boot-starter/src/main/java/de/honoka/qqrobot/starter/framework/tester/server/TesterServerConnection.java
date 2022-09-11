package de.honoka.qqrobot.starter.framework.tester.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.honoka.qqrobot.framework.Framework;
import de.honoka.qqrobot.starter.framework.tester.TesterFramework;
import de.honoka.qqrobot.starter.framework.tester.model.TesterMessage;
import de.honoka.qqrobot.starter.framework.tester.model.TesterMessageType;
import de.honoka.qqrobot.starter.framework.tester.model.TesterRobotMessage;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Objects;

@ServerEndpoint("/tester-framework/server")
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

    @SneakyThrows
    public void sendMessage(TesterMessage message) {
        this.session.getBasicRemote().sendText(TesterServer
                .gson.toJson(message));
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(String messageStr) {
        TesterMessage message = TesterServer.gson.fromJson(messageStr,
                TesterMessage.class);
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
        }
    }

    @OnClose
    public void onClose() {
        testerServer.getConnections().remove(this);
        for(TesterServerConnection connection : testerServer.getConnections()) {
            connection.sendMessage(new TesterMessage(null)
                    .setType(TesterMessageType.UESR_LOGOUT)
                    .setData(data));
        }
    }

    private TesterMessage onLogin(TesterMessage message) {
        TesterMessage res = new TesterMessage(message.getId());
        res.setType(TesterMessageType.LOGIN);
        //提取并检查消息中的成员
        JsonObject msgData = message.getData();
        long userId = msgData.get("qq").getAsLong();
        String username = msgData.get("name").getAsString();
        JsonObject resData = res.getData();
        if(ObjectUtils.anyNull(userId, username)) {
            resData.addProperty("status", false);
            resData.addProperty("message", "账号和用户名不能为空");
            return res;
        }
        //遍历现有连接，检查是否已登录
        for(TesterServerConnection connection : testerServer.getConnections()) {
            if(connection.data.get("qq").getAsLong() == userId) {
                resData.addProperty("status", false);
                resData.addProperty("message", "该账号已登录");
                return res;
            }
            if(Objects.equals(connection.data.get("name")
                    .getAsString(), username)) {
                resData.addProperty("status", false);
                resData.addProperty("message", "该用户名已存在");
                return res;
            }
            if(StringUtils.equalsIgnoreCase(connection.data
                    .get("name").getAsString(), "robot")) {
                resData.addProperty("status", false);
                resData.addProperty("message",
                        "不能使用Robot作为用户名");
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
                connection.sendMessage(new TesterMessage(null)
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
            messageData.add(connection.data.get("name").getAsString());
        }
        JsonObject resData = new JsonObject();
        resData.add("online", messageData);
        res.setData(resData);
        return res;
    }

    @SuppressWarnings("unchecked")
    private void onGroupMessage(TesterMessage message) {
        JsonArray content = message.getData().getAsJsonArray(
                "content");
        Framework<TesterRobotMessage> framework = (Framework<TesterRobotMessage>)
                testerServer.getRobotBeanHolder().getFramework();
        framework.frameworkCallback.onGroupMsg(
                TesterFramework.GROUP_NUMBER,
                data.get("qq").getAsLong(),
                framework.transform(TesterRobotMessage.of(content))
        );
        JsonObject resData = new JsonObject();
        resData.addProperty("status", true);
        sendMessage(new TesterMessage(message.getId())
                .setType(TesterMessageType.GROUP_MESSAGE_RESPONSE)
                .setData(resData)
        );
        testerServer.getExecutor().execute(() -> {
            for(TesterServerConnection connection : testerServer.getConnections()) {
                if(connection.data.get("name").getAsString().equals(
                        data.get("name").getAsString()))
                    continue;
                JsonObject notifyMsgData = new JsonObject();
                notifyMsgData.addProperty("name",
                        data.get("name").getAsString());
                notifyMsgData.add("content", content);
                connection.sendMessage(new TesterMessage(null)
                        .setType(TesterMessageType.GROUP_MESSAGE)
                        .setData(notifyMsgData)
                );
            }
        });
    }
}
