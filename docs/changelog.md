# 更新日志

## 1.1.1
#### qqrobot-spring-boot-starter 1.1.1
- 所有Component不再从`RobotBeanHolder`中获取`Framework`实例。
- 移除多个Component的构造器参数，改为字段注入，初始化逻辑转移至`@PostConstruct`注解标记的方法。
- 所有用户在Tester框架中所共同处于的群的群号，现在可通过Spring Boot配置项来指定。

#### qqrobot-framework 1.0.1
- 移除`Framework`类中带参数的构造器。

## 1.1.0
#### qqrobot-spring-boot-starter 1.1.0
- 移除`RobotImageUtils`。
- 集成原属于qqrobot实例的后台管理界面。

## 1.0.0
#### qqrobot-spring-boot-starter 1.0.0
- 起始版本

#### qqrobot-framework 1.0.0
- 起始版本
