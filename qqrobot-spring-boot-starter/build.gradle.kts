import de.honoka.gradle.buildsrc.MavenPublish.setupVersionAndPublishing

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
    implementation(libs.honoka.kotlin.utils)
    implementation(libs.honoka.framework.utils)
    implementation(libs.qqrobot.framework.api.also {
        api(it)
    })
    implementation(libs.mirai.core)
    implementation("com.google.code.gson:gson")
    implementation("com.github.houbb:opencc4j:1.6.0")
    implementation("org.hibernate.validator:hibernate-validator")
    implementation("com.h2database:h2:2.1.214")
}

tasks {
    compileJava {
        dependsOn(":qqrobot-framework-api:publish")
    }
}