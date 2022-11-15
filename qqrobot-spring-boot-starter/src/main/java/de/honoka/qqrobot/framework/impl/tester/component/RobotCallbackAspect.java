package de.honoka.qqrobot.framework.impl.tester.component;

import de.honoka.qqrobot.framework.model.RobotMultipartMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RobotCallbackAspect {

    private static final String SEPERATOR = StringUtils.repeat(
            "-----", 7);

    @Before("execution(* de.honoka.qqrobot.starter.component." +
            "DefaultFrameworkCallback.onGroupMsg(..))")
    public void logGroupMessage(JoinPoint joinPoint) {
        Long group = (Long) joinPoint.getArgs()[0];
        long qq = (long) joinPoint.getArgs()[1];
        RobotMultipartMessage msg = (RobotMultipartMessage) joinPoint.getArgs()[2];
        log.info(String.format(
                "\nTester收到群消息：\n" +
                "群号：%d，QQ：%d\n" +
                "%s\n%s\n%s",
                group, qq, SEPERATOR, msg.contentToString(), SEPERATOR
        ));
    }

    @Before("execution(* de.honoka.qqrobot.starter.component." +
            "DefaultFrameworkCallback.onPrivateMsg(..))")
    public void logPrivateMessage(JoinPoint joinPoint) {
        long qq = (long) joinPoint.getArgs()[0];
        RobotMultipartMessage msg = (RobotMultipartMessage) joinPoint.getArgs()[1];
        log.info(String.format(
                "\nTester收到私聊消息：\n" +
                "QQ：%d\n" +
                "%s\n%s\n%s",
                qq, SEPERATOR, msg.contentToString(), SEPERATOR
        ));
    }

    @Before("execution(* de.honoka.qqrobot.framework." +
            "FrameworkApi.sendGroupMsg(..))")
    public void logSendGroupMessage(JoinPoint joinPoint) {
        Long group = (Long) joinPoint.getArgs()[0];
        Object msg = joinPoint.getArgs()[1];
        log.info(String.format(
                "\nTester发送群消息：\n" +
                "群号：%d\n" +
                "%s\n%s\n%s",
                group, SEPERATOR, msg, SEPERATOR
        ));
    }

    @Before("execution(* de.honoka.qqrobot.framework." +
            "FrameworkApi.sendPrivateMsg(..))")
    public void logSendPrivateMessage(JoinPoint joinPoint) {
        long qq = (long) joinPoint.getArgs()[0];
        Object msg = joinPoint.getArgs()[1];
        log.info(String.format(
                "\nTester发送私聊消息：\n" +
                "QQ：%d\n" +
                "%s\n%s\n%s",
                qq, SEPERATOR, msg, SEPERATOR
        ));
    }

    @Before("execution(* de.honoka.qqrobot.framework." +
            "FrameworkApi.reply(..))")
    public void logReplyMessage(JoinPoint joinPoint) {
        Long group = (Long) joinPoint.getArgs()[0];
        long qq = (long) joinPoint.getArgs()[1];
        Object msg = joinPoint.getArgs()[2];
        log.info(String.format(
                "\nTester发送群回复：\n" +
                "群号：%d，QQ：%d\n" +
                "%s\n%s\n%s",
                group, qq, SEPERATOR, msg, SEPERATOR
        ));
    }
}
