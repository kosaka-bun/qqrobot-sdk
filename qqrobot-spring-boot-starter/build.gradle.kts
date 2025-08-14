plugins {
    alias(libs.plugins.kotlin.spring)
}

version = libs.versions.p.qqrobot.spring.boot.starter.get()

honoka.basic.dependencies {
    springBootBom()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    api(libs.qsbs.honoka.spring.boot.starter)
    api(libs.qsbs.qqrobot.framework.api)
    val configProcessor = "org.springframework.boot:spring-boot-configuration-processor:${
        libs.versions.d.spring.boot.get()
    }"
    kapt(configProcessor)
    implementation("com.github.houbb:opencc4j:1.6.0")
    implementation("com.h2database:h2:2.1.214")
}

honoka.basic.publishing {
    default()
}
