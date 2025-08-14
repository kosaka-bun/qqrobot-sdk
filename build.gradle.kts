import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.charset.StandardCharsets

plugins {
    java
    `java-library`
    `maven-publish`
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.lombok)
    alias(libs.plugins.honoka.basic)
}

group = "de.honoka.qqrobot"
version = libs.versions.p.root.get()

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "org.jetbrains.kotlin.plugin.lombok")
    apply(plugin = "de.honoka.gradle.plugin.basic")

    group = rootProject.group

    java {
        toolchain.languageVersion = JavaLanguageVersion.of(17)
        withSourcesJar()
    }

    honoka.basic.dependencies {
        kotlin()
        lombok()
    }

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    }

    tasks {
        withType<JavaCompile> {
            options.run {
                encoding = StandardCharsets.UTF_8.name()
                val compilerArgs = compilerArgs as MutableCollection<String>
                compilerArgs += listOf("-parameters")
            }
        }
        
        withType<KotlinCompile> {
            compilerOptions {
                freeCompilerArgs.addAll("-Xjsr305=strict", "-Xjvm-default=all")
            }
        }

        withType<Test> {
            useJUnitPlatform()
        }
    }
    
    kapt {
        keepJavacAnnotationProcessors = true
    }
}

honoka.basic.publishing {
    defineCheckVersionTask()
}

//仅用于避免libs.versions.toml中产生version变量未使用的提示
libs.versions.d.lombok
libs.versions.d.kotlin.coroutines
libs.versions.d.spring.boot
