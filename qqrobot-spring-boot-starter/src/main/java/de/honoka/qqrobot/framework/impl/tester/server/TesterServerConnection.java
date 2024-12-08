package de.honoka.qqrobot.framework.impl.tester.server;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import de.honoka.qqrobot.framework.api.Framework;
import de.honoka.qqrobot.framework.impl.tester.config.TesterProperties;
import de.honoka.qqrobot.framework.impl.tester.model.TesterMessage;
import de.honoka.qqrobot.framework.impl.tester.model.TesterMessageType;
import de.honoka.qqrobot.framework.impl.tester.model.TesterRobotMessage;
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

    private JSONObject data;

    @SuppressWarnings("unused")
    public TesterServerConnection() {}

    public TesterServerConnection(TesterServer server) {
        testerServer = server;
    }

    public void sendMessage(TesterMessage message) {
        try {
            this.session.getBasicRemote().sendText(JSONUtil.toJsonStr(message));
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
        TesterMessage message = JSONUtil.toBean(messageStr, TesterMessage.class);
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
        JSONObject msgData = message.getData();
        long qq = msgData.getLong("qq");
        String username = msgData.getStr("name");
        JSONObject resData = res.getData();
        if(StrUtil.equalsIgnoreCase(username, "robot")) {
            resData.set("status", false);
            resData.set("message", "不能使用Robot作为用户名");
            return res;
        }
        if(!ObjectUtil.isAllNotEmpty(qq, username) || username.isEmpty()) {
            resData.set("status", false);
            resData.set("message", "账号和用户名不能为空");
            return res;
        }
        //遍历现有连接，检查是否已登录
        for(TesterServerConnection connection : testerServer.getConnections()) {
            if(connection.data.getLong("qq") == qq) {
                resData.set("status", false);
                resData.set("message", "该账号已登录");
                return res;
            }
            if(Objects.equals(connection.data.getStr("name"), username)) {
                resData.set("status", false);
                resData.set("message", "该用户名已存在");
                return res;
            }
        }
        data = JSONUtil.parseObj(msgData.toString());
        testerServer.getConnections().add(this);
        //登录成功，通知所有在线用户
        testerServer.getExecutor().execute(() -> {
            for(TesterServerConnection connection : testerServer.getConnections()) {
                if(connection.data.getStr("name").equals(username))
                    continue;
                JSONObject notifyMsgData = new JSONObject();
                notifyMsgData.set("name", username);
                notifyMsgData.set("qq", qq);
                notifyMsgData.set("data", data);
                connection.sendMessage(
                    new TesterMessage(null)
                        .setType(TesterMessageType.NEW_USER_LOGIN)
                        .setData(notifyMsgData)
                );
            }
        });
        res.getData().set("status", true);
        return res;
    }

    private TesterMessage queryOnline(TesterMessage message) {
        TesterMessage res = new TesterMessage(message.getId());
        JSONArray messageData = new JSONArray();
        for(TesterServerConnection connection : testerServer.getConnections()) {
            JSONObject item = new JSONObject();
            item.set("name", connection.data.getStr("name"));
            item.set("data", connection.data);
            messageData.add(item);
        }
        JSONObject resData = new JSONObject();
        resData.set("online", messageData);
        res.setData(resData);
        return res;
    }

    @SuppressWarnings("unchecked")
    private void onGroupMessage(TesterMessage message) {
        //收到消息，发送回执
        JSONObject resData = new JSONObject();
        resData.set("status", true);
        sendMessage(
            new TesterMessage(message.getId())
                .setType(TesterMessageType.GROUP_MESSAGE_RESPONSE)
                .setData(resData)
        );
        //提醒其他用户
        JSONArray content = message.getData().getJSONArray("content");
        testerServer.getExecutor().execute(() -> {
            for(TesterServerConnection connection : testerServer.getConnections()) {
                if(connection.data.getStr("name").equals(data.getStr("name")))
                    continue;
                JSONObject notifyMsgData = new JSONObject();
                notifyMsgData.set("name", data.getStr("name"));
                notifyMsgData.set("content", content);
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
            data.getLong("qq"),
            framework.transform(TesterRobotMessage.of(content))
        );
    }

    @SuppressWarnings("unchecked")
    private void onPrivateMessage(TesterMessage message) {
        //收到消息，发送回执
        JSONObject resData = new JSONObject();
        resData.set("status", true);
        sendMessage(
            new TesterMessage(message.getId())
                .setType(TesterMessageType.PRIVATE_MESSAGE_RESPONSE)
                .setData(resData)
        );
        //处理消息
        JSONArray content = message.getData().getJSONArray("content");
        Framework<TesterRobotMessage> framework = (Framework<TesterRobotMessage>) testerServer.getFramework();
        framework.getFrameworkCallback().onPrivateMsg(
            data.getLong("qq"),
            framework.transform(TesterRobotMessage.of(content))
        );
    }
}
