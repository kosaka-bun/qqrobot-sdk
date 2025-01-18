package de.honoka.qqrobot.starter.command;

import de.honoka.qqrobot.framework.api.model.RobotMessage;
import de.honoka.qqrobot.framework.api.model.RobotMessageType;
import lombok.Getter;

/**
 * 命令方法参数类型，被@Command注解的类必须采用此参数类型
 */
@SuppressWarnings({ "unused", "ClassCanBeRecord" })
@Getter
public class CommandMethodArgs {

    /**
     * 调用命令的qq号和其所在群号
     */
    private final Long group;

    private final long qq;

    /**
     * 为调用的命令提供的参数列表
     */
    private final Object[] args;

    public CommandMethodArgs(Long group, long qq, Object[] args) {
        this.group = group;
        this.qq = qq;
        this.args = args;
    }
    
    private void ensureIndexValid(int index) {
        if(index < args.length) return;
        throw new CommandExceptions.IndexOutOfBoundsException(index, args.length - 1);
    }

    public int getInt(int index) {
        ensureIndexValid(index);
        try {
            return Integer.parseInt((String) args[index]);
        } catch(NumberFormatException e) {
            throw new CommandExceptions.WrongNumberParameterException((String) args[index]);
        }
    }

    public double getDouble(int index) {
        ensureIndexValid(index);
        try {
            return Double.parseDouble((String) args[index]);
        } catch(NumberFormatException e) {
            throw new CommandExceptions.WrongNumberParameterException((String) args[index]);
        }
    }

    public long getLong(int index) {
        ensureIndexValid(index);
        try {
            return Long.parseLong((String) args[index]);
        } catch(NumberFormatException e) {
            throw new CommandExceptions.WrongNumberParameterException((String) args[index]);
        }
    }

    public String getString(int index) {
        ensureIndexValid(index);
        Object arg = args[index];
        if(arg instanceof String) return (String) arg;
        else return arg.toString();
    }

    @SuppressWarnings("unchecked")
    public RobotMessage<Long> getAt(int index) {
        ensureIndexValid(index);
        Object arg = args[index];
        if(arg instanceof RobotMessage<?> message) {
            if(message.getType().equals(RobotMessageType.AT)) {
                return (RobotMessage<Long>) message;
            }
        }
        throw new CommandExceptions.WrongAtParameterException();
    }
}
