plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.kotlin.spring)
}

version = libs.versions.p.file.receiver.get()

honoka.basic {
    dependencies {
        springBootBom()
        springBootConfigProcessor()
    }
}

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
