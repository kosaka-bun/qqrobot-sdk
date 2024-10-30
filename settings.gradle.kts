@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        maven("https://maven.aliyun.com/repository/public")
        mavenCentral()
        maven("https://mirrors.honoka.de/maven-repo/release")
        maven("https://mirrors.honoka.de/maven-repo/development")
    }
}

pluginManagement {
    repositories {
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "qqrobot-sdk"

include("qqrobot-framework-api")
include("qqrobot-spring-boot-starter")
include("qqrobot-spring-boot-starter:web")
include("qqrobot-spring-boot-starter:web:admin")
include("qqrobot-spring-boot-starter:web:tester")
include("file-receiver")