package de.honoka.qqrobot.starter.common.annotation;

import java.lang.annotation.*;

/**
 * 所有含有此注解的方法均可作为命令使用
 */
@Target(ElementType.METHOD)
//必须声明为runtime，否则获取Annotation时获取不到
@Retention(RetentionPolicy.RUNTIME)
//该注解可以被子类所继承，即若父类中某方法被注解，那么子类所继承的那个方法也视为带有此注解
//用于在对象被CGLIB代理后，getClass所获取到的类中的方法没有注解的问题
@Inherited
public @interface Command {
	
	/*
	 * 声明为value的注解值，在为目标添加注解填写注解值时，不用显式填写"value ="
	 * @Command("abc") = @Command(value = "abc)
	 *
	 * 声明了默认值的注解值，在为目标添加注解时，可以不用指定该注解值，否则必须指定
	 * int id() default -1;
	 * String[] parameters() default {};
	 */
	
	/**
	 * 命令的名字，可以有多个
	 */
	String[] value();

	/**
	 * 该命令是否必须由机器人管理员调用
	 */
	boolean admin() default false;

	/**
	 * 该命令是否不需要带命令前缀
	 */
	boolean noPrefix() default false;

	/**
	 * 该命令最少需要多少个参数
	 */
	int argsNum() default 0;
}
