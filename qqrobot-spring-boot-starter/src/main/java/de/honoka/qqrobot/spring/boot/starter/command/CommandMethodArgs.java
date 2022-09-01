package de.honoka.qqrobot.spring.boot.starter.command;

import lombok.Getter;

/**
 * 命令方法参数类型，被@Command注解的类必须采用此参数类型
 */
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
	private final String[] args;

	public CommandMethodArgs(Long group, long qq, String[] args) {
		this.group = group;
		this.qq = qq;
		this.args = args;
	}
	
	public static class WrongNumberParameterException extends RuntimeException {
		
		public WrongNumberParameterException() {
		}
		
		public WrongNumberParameterException(String message) {
			super(message);
		}
	}
	
	public int getInt(int index) {
		try {
			return Integer.parseInt(args[index]);
		} catch (NumberFormatException e) {
			throw new WrongNumberParameterException(args[index]);
		}
	}
	
	public double getDouble(int index) {
		try {
			return Double.parseDouble(args[index]);
		} catch (NumberFormatException e) {
			throw new WrongNumberParameterException(args[index]);
		}
	}
	
	public long getLong(int index) {
		try {
			return Long.parseLong(args[index]);
		} catch (NumberFormatException e) {
			throw new WrongNumberParameterException(args[index]);
		}
	}
	
	public String getString(int index) {
		if(index < args.length)
			return args[index];
		else
			return null;
	}
}
