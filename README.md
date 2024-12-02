# QQ Robot SDK
![Java](./docs/img/Java-17-brightgreen.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen?logo=Spring)
![Kotlin](https://img.shields.io/badge/Kotlin-1.8.10-brightgreen?logo=Kotlin)<br />
[![License](https://img.shields.io/github/license/kosaka-bun/qqrobot-sdk?label=License&color=blue&logo=GitHub)](./LICENSE)
![GitHub Stars](https://img.shields.io/github/stars/kosaka-bun/qqrobot-sdk?label=Stars&logo=GitHub&style=flat)
[![Release](https://img.shields.io/github/release/kosaka-bun/qqrobot-sdk?label=Release&logo=GitHub)](../../releases)

## 简介
本项目是一款Spring Boot平台一站式QQ机器人开发框架，其对QQ机器人开发过程中遇到的许多常见问题提供了便利的解决方案，能够快速、高效、便捷地搭建基于Spring Boot平台的QQ机器人。

项目分为两个模块，分别是qqrobot-framework-api和qqrobot-spring-boot-starter。qqrobot-framework-api规定了一个QQ机器人框架需要遵循的规范，qqrobot-spring-boot-starter为基于qqrobot-framework-api实现的一站式QQ机器人应用开发框架，其特性包含自动化命令匹配、命令权限校验、命令参数提取、便利的持续会话实现、调用日志与异常日志等。同时还包含了便捷的测试框架以及后台管理界面。

本项目采用Apache-2.0 License，使用本项目时，请遵守此开源许可证的相关规定。

请参阅：[开发文档](./docs/development.md)&emsp;[更新日志](./docs/changelog.md)&emsp;[Tester框架说明文档](./docs/tester-framework.md)

## 使用
本项目部署于：

[![maven-repo](https://github-readme-stats.vercel.app/api/pin/?username=kosaka-bun&repo=maven-repo)](https://github.com/kosaka-bun/maven-repo)

使用前请先阅读此仓库的文档，为你的Maven或Gradle添加依赖仓库。

各模块版本号请前往[Releases](../../releases)查看。

### Maven
```xml
<dependencies>
    <dependency>
        <groupId>de.honoka.qqrobot</groupId>
        <artifactId>qqrobot-spring-boot-starter</artifactId>
        <version>版本号</version>
    </dependency>
</dependencies>
```

### Gradle
```groovy
dependencies {
    implementation 'de.honoka.qqrobot:qqrobot-spring-boot-starter:版本号'
}
```

## 快速开始
[qqrobot-demo](https://github.com/kosaka-bun/demo-projects/tree/master/qqrobot-demo)是基于本框架搭建的一个示例程序，其包含了本框架的所有基本用法示例。请先按步骤分别尝试在测试环境与正式环境中尝试运行此Demo应用。

### 测试环境
1. clone [demo-projects仓库](https://github.com/kosaka-bun/demo-projects)。
2. 使用IDE打开仓库中的qqrobot-demo目录（非仓库根目录）。
3. 运行qqrobot-demo的Spring Boot主类[QqRobotDemo](https://github.com/kosaka-bun/demo-projects/tree/master/qqrobot-demo/src/main/java/de/honoka/qqrobot/demo/QqRobotDemo.java)。
4. 等待应用启动完成，打开测试框架界面，默认为[http://localhost:8081/qqrobot-demo/tester-framework/index.html](http://localhost:8081/qqrobot-demo/tester-framework/index.html)。

![](./docs/img/1.png)

![](./docs/img/2.png)
5. 点击“连接”，然后在群聊消息对应的输入框中输入“%菜单”，若得到回复，则测试环境运行成功。

![](./docs/img/3.png)

### 正式环境
1. 在qqrobot-demo目录下，执行`gradlew bootJar`命令，等待构建完成。
2. 将[qqrobot-demo/src/main/resources/config/application-prod.yml](https://github.com/kosaka-bun/demo-projects/tree/master/qqrobot-demo/src/main/resources/config/application-prod.yml)文件，复制到qqrobot-demo/build/libs目录下。
3. 打开libs目录下的application-prod.yml文件，修改`honoka.qqrobot`项下的`qq`、`password`、`admin-qq`、`developing-group`四个配置项，指定机器人要使用的QQ账号、密码、可调用管理员命令的用户的QQ账号，以及开发群的群号。
4. 在libs目录下创建`startup.bat`（Windows）或`startup.sh`（Linux）文件，插入以下内容。
```shell
javaw -jar -Dfile.encoding=UTF-8 -Dspring.profiles.active=prod [jar包的名称]
```
5. 执行`startup.bat`或`startup.sh`文件，等待应用启动完成，注意留意jar包是否启用了prod配置文件。

![](./docs/img/4.png)

6. 通过任何方式向机器人所登录QQ账户发送`%菜单`命令，若收到回复，则应用在正式环境中启动成功。

### 注意事项
- 本文中所有操作均以Windows图形界面为默认环境，默认在图形界面下进行部署，如需在控制台中运行和部署，请将步骤中的`javaw`命令替换为`java`命令。

## 开发
请参阅[开发文档](./docs/development.md)，通过其中的说明了解如何基于此框架开发QQ机器人应用。

有关此框架附带的测试框架（Tester Framework）的相关信息，请参阅[Tester框架说明文档](./docs/tester-framework.md)。
