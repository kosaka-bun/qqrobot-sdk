@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.kotlin.jpa)
}

apply(plugin = "org.jetbrains.kotlin.plugin.spring")

version = libs.versions.file.receiver.get()

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(libs.honoka.spring.boot.starter)
}

tasks {
    bootJar {
        archiveFileName.set("${project.name}.jar")
    }
}