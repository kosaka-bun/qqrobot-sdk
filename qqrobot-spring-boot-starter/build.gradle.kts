import de.honoka.gradle.buildsrc.MavenPublish.setupVersionAndPublishing
import de.honoka.gradle.buildsrc.implementationApi

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.spring)
}

setupVersionAndPublishing(libs.versions.qqrobot.spring.boot.starter.get())

dependencyManagement {
    imports {
        //必须按照顺序导入，后导入的依赖配置将覆盖先导入的相同依赖的配置
        mavenBom(libs.spring.boot.dependencies.get().toString())
        mavenBom(libs.kotlin.bom.get().toString())
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-configuration-processor".also {
        annotationProcessor(it)
    })
    implementation("de.honoka.sdk:honoka-spring-boot-starter:1.0.1-dev")
    implementationApi("de.honoka.qqrobot:qqrobot-framework-api:2.0.0")
    compileOnly(libs.mirai.core)
    compileOnly(libs.mirai.console.compiler.annotations)
    implementation("com.google.code.gson:gson")
    implementation("com.github.houbb:opencc4j:1.6.0")
    implementation("org.hibernate.validator:hibernate-validator")
    implementation("com.h2database:h2:2.1.214")
}

tasks {
    compileKotlin {
        dependsOn(":qqrobot-framework-api:publish")
    }
}