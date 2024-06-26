import de.honoka.gradle.buildsrc.MavenPublish.setupVersionAndPublishing
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin)
}

setupVersionAndPublishing(libs.versions.qqrobot.spring.boot.starter.get())

dependencyManagement {
    imports {
        mavenBom(libs.spring.boot.dependencies.get().toString())
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
    implementation("de.honoka.sdk:honoka-utils:1.0.6")
    implementation("de.honoka.sdk:honoka-framework-utils:1.0.0")
    implementation("de.honoka.qqrobot:qqrobot-framework-api:1.0.3".also {
        api(it)
    })
    implementation(libs.mirai.core)
    implementation("com.google.code.gson:gson")
    implementation("com.github.houbb:opencc4j:1.6.0")
    implementation("org.hibernate.validator:hibernate-validator")
    implementation("com.h2database:h2:2.1.214")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = java.sourceCompatibility.toString()
        }
    }
}