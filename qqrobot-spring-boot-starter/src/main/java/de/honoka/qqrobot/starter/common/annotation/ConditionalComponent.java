package de.honoka.qqrobot.starter.common.annotation;

import java.lang.annotation.*;

/**
 * 用于表示这个类被哪个配置类所按条件加载
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface ConditionalComponent {

    Class<?> value();
}
