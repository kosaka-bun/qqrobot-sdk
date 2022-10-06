package de.honoka.qqrobot.starter.command;

import de.honoka.qqrobot.framework.model.RobotMessage;
import de.honoka.qqrobot.framework.model.RobotMultipartMessage;
import de.honoka.qqrobot.starter.RobotBasicProperties;
import de.honoka.qqrobot.starter.common.ConstantMessage;
import de.honoka.qqrobot.starter.common.annotation.Command;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 命令调用器，存储命令的名称、要调用的命令的方法、用于调用此方法的对象
 */
@Getter
public class CommandInvoker {

    /**
     * 命令名
     */
    private final List<String> commandName;

    /**
     * 该命令是否必须由机器人管理员调用
     */
    private final boolean mustInvokeByAdmin;

    /**
     * 该命令是否不需要带命令前缀
     */
    private final boolean noPrefix;

    /**
     * 该命令最少需要多少个参数
     */
    private final int argsNum;

    /**
     * 调用此命令时要调用的方法
     */
    private final Method method;

    private final Class<?>[] parameterTypes;

    /**
     * 调用命令方法时需要使用的对象
     */
    private final Object controller;

    private RobotBasicProperties basicProperties;

    //私有，仅在内部构造实例，外部获取实例需调用静态方法
    private CommandInvoker(List<String> commandName, boolean mustInvokeByAdmin,
                           boolean noPrefix, int argsNum, Method method,
                           Object controller) {
        this.commandName = commandName;
        this.mustInvokeByAdmin = mustInvokeByAdmin;
        this.noPrefix = noPrefix;
        this.argsNum = argsNum;
        this.method = method;
        this.parameterTypes = method.getParameterTypes();
        this.controller = controller;
    }

    /**
     * 执行此命令，得到执行完成后的回复
     */
    @SneakyThrows
    public RobotMultipartMessage invoke(Long group, long qq, Object[] args) {
        Long adminQq = basicProperties.getAdminQq();
        //判断是否是管理员命令，且已配置管理员QQ号
        if(mustInvokeByAdmin && adminQq != null) {
            //鉴权，判断调用者是否是管理员
            if(qq != adminQq) {
                return RobotMultipartMessage.of(ConstantMessage.NO_AUTHORIZATION);
            }
        }
        //构造调用Command方法的参数
        CommandMethodArgs argsObject = new CommandMethodArgs(group, qq, args);
        Object[] methodArgs = new Object[parameterTypes.length];
        for(int i = 0; i < parameterTypes.length; i++) {
            if(parameterTypes[i].equals(CommandMethodArgs.class))
                methodArgs[i] = argsObject;
        }
        //调用
        try {
            Object result = method.invoke(controller, methodArgs);
            //如果调用的方法返回类型为void，或方法返回了null，则不能toString
            if(result == null) return null;
            if(result instanceof RobotMultipartMessage) {
                return (RobotMultipartMessage) result;
            } else if(result instanceof RobotMessage) {
                return RobotMultipartMessage.of((RobotMessage<?>) result);
            } else {
                return RobotMultipartMessage.of(result.toString());
            }
        } catch(Throwable t) {
            while(t.getCause() != null) {
                //noinspection AssignmentToCatchBlockParameter
                t = t.getCause();
            }
            if(t instanceof CommandMethodArgs.WrongNumberParameterException) {
                return RobotMultipartMessage.of("你提供的参数有误，应当提供数字");
            } else if(t instanceof CommandMethodArgs.WrongAtParameterException) {
                return RobotMultipartMessage.of("你提供的参数有误，应当提供一个At");
            }
            throw t;
        }
    }

    /**
     * 提供包含命令方法的类，提取所有合格的命令方法，组装为调用器，返回调用器列表
     */
    @SneakyThrows
    public static List<CommandInvoker> getInvokers(List<Object> controllers,
            RobotBasicProperties basicProperties) {
        List<CommandInvoker> invokers = new ArrayList<>();
        //遍历每个命令控制器类
        for(Object controller : controllers) {
            Class<?> controllerClass = controller.getClass();
            //判断是否是cglib代理的类，若是则获取原始的类
            try {
                controllerClass = (Class<?>) controllerClass.getMethod(
                        "getTargetClass").invoke(controller);
            } catch(Throwable t) {
                //ignore
            }
            //获取所有方法，提取含有Command注解的方法
            Method[] methods = controllerClass.getMethods();
            for(Method method : methods) {
                //获取注解在这个方法上的Command注解
                Command command = method.getAnnotation(Command.class);
                //此方法上没有Command注解
                if(command == null) continue;
                //若从此方法上获取到了Command注解，则此方法是一个命令方法
                //将该方法的信息加入命令列表中
                List<String> commandName = Arrays.asList(command.value());
                CommandInvoker invoker = new CommandInvoker(commandName,
                        command.admin(), command.noPrefix(), command.argsNum(),
                        method, controller);
                invoker.basicProperties = basicProperties;
                invokers.add(invoker);
            }
        }
        return invokers;
    }
}
