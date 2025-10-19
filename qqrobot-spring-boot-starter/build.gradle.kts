plugins {
    alias(libs.plugins.kotlin.spring)
}

honoka.basic.publishing.version = libs.versions.p.qqrobot.spring.boot.starter.get()

honoka.basic {
    dependencies {
        springBootBom()
        springBootConfigProcessor()
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    api(libs.honoka.spring.boot.starter)
    api(libs.qqrobot.framework.api)
    implementation("com.github.houbb:opencc4j:1.6.0")
    implementation("com.h2database:h2:2.1.214")
}
