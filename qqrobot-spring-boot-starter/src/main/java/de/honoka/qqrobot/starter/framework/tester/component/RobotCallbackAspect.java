package de.honoka.qqrobot.starter.framework.tester.component;

import de.honoka.qqrobot.framework.model.RobotMultipartMessage;
import de.honoka.qqrobot.starter.common.annotation.ConditionalComponent;
import de.honoka.qqrobot.starter.framework.FrameworkBeans;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@ConditionalComponent(FrameworkBeans.class)
@Slf4j
@Aspect
public class RobotCallbackAspect {

    @Before("execution(* de.honoka.qqrobot.starter.component." +
            "DefaultFrameworkCallback.onGroupMsg(..))")
    public void logGroupMessage(JoinPoint joinPoint) {
        Long group = (Long) joinPoint.getArgs()[0];
        long qq = (long) joinPoint.getArgs()[1];
        RobotMultipartMessage msg = (RobotMultipartMessage) joinPoint.getArgs()[2];
        log.info(String.format(
                "\nTester收到群消息：\n" +
                "群号：%d，QQ：%d\n%s",
                group, qq, msg.contentToString()
        ));
    }

    @Before("execution(* de.honoka.qqrobot.starter.component." +
            "DefaultFrameworkCallback.onPrivateMsg(..))")
    public void logPrivateMessage(JoinPoint joinPoint) {
        long qq = (long) joinPoint.getArgs()[0];
        RobotMultipartMessage msg = (RobotMultipartMessage) joinPoint.getArgs()[1];
        log.info(String.format(
                "\nTester收到私聊消息：\n" +
                "QQ：%d\n%s",
                qq, msg.contentToString()
        ));
    }

    @Before("execution(* de.honoka.qqrobot.framework." +
            "FrameworkApi.sendGroupMsg(..))")
    public void logSendGroupMessage(JoinPoint joinPoint) {
        Long group = (Long) joinPoint.getArgs()[0];
        Object msg = joinPoint.getArgs()[1];
        log.info(String.format(
                "\nTester发送群消息：\n" +
                "群号：%d\n%s",
                group, msg
        ));
    }

    @Before("execution(* de.honoka.qqrobot.framework." +
            "FrameworkApi.sendPrivateMsg(..))")
    public void logSendPrivateMessage(JoinPoint joinPoint) {
        long qq = (long) joinPoint.getArgs()[0];
        Object msg = joinPoint.getArgs()[1];
        log.info(String.format(
                "\nTester发送私聊消息：\n" +
                "QQ：%d\n%s",
                qq, msg
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
                "群号：%d，QQ：%d\n%s",
                group, qq, msg
        ));
    }
}
