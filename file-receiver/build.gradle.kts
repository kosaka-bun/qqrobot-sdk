plugins {
    alias(libs.plugins.spring.boot)
    /*
     * 不能同时在plugins块中导入kotlin-spring与kotlin-jpa插件，否则Gradle将报告Plugin with
     * id 'org.jetbrains.kotlin.plugin.spring' was already requested.
     *
     * kotlin-jpa插件在导入时会一并导入kotlin-spring插件。
     */
    alias(libs.plugins.kotlin.jpa)
}

version = libs.versions.p.file.receiver.get()

honoka.basic.dependencies {
    springBootBom()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(libs.fr.honoka.spring.boot.starter)
}

tasks {
    bootJar {
        archiveFileName.set("${project.name}.jar")
    }
}
