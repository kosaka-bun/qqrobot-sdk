import de.honoka.gradle.buildsrc.kotlin

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.kotlin.jpa)
}

apply(plugin = "org.jetbrains.kotlin.plugin.spring")

version = libs.versions.file.receiver.get()

dependencies {
    kotlin(project)
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(libs.honoka.spring.boot.starter)
    //仅用于避免libs.versions.toml中产生version变量未使用的提示
    libs.versions.kotlin.coroutines
}

tasks {
    bootJar {
        archiveFileName.set("${project.name}.jar")
    }
}