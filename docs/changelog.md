# 更新日志

## 2.0.0
#### 工程
- 新增file-receiver项目，用于辅助qqrobot-spring-boot-starter的OneBot实现，在OneBot框架所在的操作系统中接受机器人应用要发送的图片内容。
- 移动qqrobot-spring-boot-starter/src/main/web至qqrobot-spring-boot-starter目录下，并将其与其子项目（admin、tester）设置为gradle子项目。
- 所有项目均改为最低支持Java 17。
- 需要引入Kotlin的项目统一采用Kotlin 1.8.10与Kotlin Coroutines 1.6.4版本。

#### qqrobot-framework-api 2.0.0
- 适配Java 17。
- 更改顶级包路径为`de.honoka.qqrobot.framework.api`。
- 更新`FrameworkApi`与`FrameworkCallback`。

#### qqrobot-spring-boot-starter 2.0.0
- 适配Spring Boot 3.2.5。
- 适配qqrobot-framework-api 2.0.0。
- 适配honoka-spring-boot-starter 1.0.0。
- 实现对OneBot 11标准的支持。

#### file-receiver 1.0.0
- 初始版本，实现图片与文件接收接口，返回存储的文件所在的URI路径。

## 1.3.4
#### qqrobot-spring-boot-starter 1.3.4
- 适配mirai 2.16.0版本。

## 1.3.3
#### qqrobot-spring-boot-starter 1.3.3
- 适配mirai 2.14.0版本。

## 1.3.2
#### qqrobot-framework-api 1.0.3
- 优化API。

#### qqrobot-spring-boot-starter 1.3.2
- 1.3.0版本中修改了`MessageExecutor`记录日志的逻辑，记录时使用`RobotMultipartMessage reply`的拷贝。但由于`reply.clone()`方法在异步代码块内执行，所以在此操作被执行时，`reply`对象很可能已经被修改了，从而使得在拷贝后的结果中，仍然会包含不重要的内容。此版本对这个问题进行了修复。

## 1.3.1
#### qqrobot-spring-boot-starter 1.3.1
- 为`MiraiFramework`添加定时方法，检查在线状态，并执行重连。

## 1.3.0
#### qqrobot-framework-api 1.0.2
- qqrobot-framework更名为qqrobot-framework-api。
- 为`RobotMultipartMessage`实现`clone()`方法。

#### qqrobot-spring-boot-starter 1.3.0
- 重构代码目录结构，将framework目录移出starter目录。
- framework目录不再被配置类所扫描，而是扫描framework/config，通过其中的配置类决定扫描哪个框架的实现所在的包。
- 优化mirai框架在重新登录时的逻辑，当`MiraiFramework`中的`Bot`被close时，可重新构建`Bot`。
- 修改`MessageExecutor`记录日志的逻辑，记录时使用`RobotMultipartMessage reply`的拷贝，而不是`reply`本身。因为`reply`对象可能会在被**异步**记录到日志中之前被再次修改，使得部分不重要的内容被记录到日志中。

## 1.2.2
#### qqrobot-spring-boot-starter 1.2.2
- 解决潜在的`Logger`的JDBC URL路径错误问题。

## 1.2.1
#### qqrobot-spring-boot-starter 1.2.1
- 增加`RobotConsole`以接管`System.out`与`System.err`，缓存这些输出流所输出的内容。
- 增强`RobotConsoleWindow`以支持`RobotConsole`。
- 修改`AdminApiController`以支持`RobotConsoleWindow`。

## 1.2.0
#### qqrobot-spring-boot-starter 1.2.0
- `TesterProperties`的`webPrefix`转为静态常量。
- 修复Tester Web界面的at功能bug。
- 为Robot封装通用的`ConsoleWindow`。
- 解决`AdminLoginInterceptor`潜在的错误拦截问题。
- 优化使用记录界面的表格列宽度。
- 使用函数式接口更新会话创建方式。

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
