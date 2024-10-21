import de.honoka.gradle.buildsrc.MavenPublish.setupVersionAndPublishing
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.spring)
    /*
     * Lombok Kotlin compiler plugin is an experimental feature.
     * See: https://kotlinlang.org/docs/components-stability.html.
     */
    alias(libs.plugins.kotlin.lombok)
}

setupVersionAndPublishing(libs.versions.qqrobot.spring.boot.starter.get())

dependencies {
    compileOnly(platform(libs.spring.boot.dependencies))
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    /*
     * spring-boot-configuration-processor的作用是生成配置的元数据信息，即META-INF目录下的
     * spring-configuration-metadata.json文件，从而告诉Spring这个jar包中有哪些自定义的配置。
     */
    compileOnly("org.springframework.boot:spring-boot-configuration-processor".also {
        //若不手动添加版本号，则annotationProcessor无法读取到指定依赖的版本号
        annotationProcessor("$it:${libs.versions.spring.boot.get()}")
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
    
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = java.sourceCompatibility.toString()
        }
    }
}