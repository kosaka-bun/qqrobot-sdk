package de.honoka.qqrobot.demo;

import de.honoka.qqrobot.framework.Framework;
import de.honoka.qqrobot.framework.model.RobotMessage;
import de.honoka.qqrobot.framework.model.RobotMultipartMessage;
import de.honoka.qqrobot.starter.command.CommandMethodArgs;
import de.honoka.qqrobot.starter.common.annotation.Command;
import de.honoka.qqrobot.starter.common.annotation.RobotController;
import de.honoka.qqrobot.starter.component.session.SessionManager;
import de.honoka.sdk.util.various.ImageUtils;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import java.io.InputStream;

@SuppressWarnings("unused")
@RobotController
public class DemoController {

    @Lazy
    @Resource
    private Framework<?> framework;

    @Resource
    private SessionManager sessionManager;

    @Command("命令1")
    public String command1() {
        return "调用了命令1";
    }

    @Command(value = "命令2", noPrefix = true)
    public String command2() {
        return "调用了命令2";
    }

    @Command(value = "命令3", admin = true)
    public String command3() {
        return "调用了命令3";
    }

    @Command("图片")
    public RobotMessage<InputStream> image() {
        return RobotMessage.image(ImageUtils.textToImage("这是一条图片信息"));
    }

    @Command("图文")
    public RobotMultipartMessage multipartMessage() {
        RobotMultipartMessage message = new RobotMultipartMessage();
        message.add(RobotMessage.image(ImageUtils.textToImage("这是一条图片信息")));
        message.add(RobotMessage.text("这是一条文字信息"));
        return message;
    }

    @Command("私聊")
    public void privateMsg(CommandMethodArgs args) {
        framework.sendPrivateMsg(args.getQq(), "发送私聊");
    }

    @Command(value = "at测试", argsNum = 1)
    public String atTest(CommandMethodArgs args) {
        RobotMessage<Long> at = args.getAt(0);
        String str = "qq: " + at.getContent();
        str += "\nusername: " + framework.getNickOrCard(0L, at.getContent());
        return str;
    }

    @Command("异常")
    public void exception() {
        throw new RuntimeException("Test");
    }

    @Command("会话")
    public void session(CommandMethodArgs args) {
        sessionManager.openSession(args.getGroup(), args.getQq(), session -> {
            session.reply("会话已开启，现在会回复你每条消息的字符个数，" +
                    "不会响应其他命令\n" +
                    "输入exit退出会话，20秒内不回复自动结束会话");
            for(; ; ) {
                String str = session.waitingForReply(20)
                        .contentToString();
                if(str.trim().equals("exit")) {
                    session.reply("会话退出");
                    break;
                }
                session.reply("内容：" + str + "\n字符数：" + str.length());
            }
        }, session -> {
            session.reply("自定义的会话超时信息");
        });
    }
}
