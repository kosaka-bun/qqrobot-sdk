import de.honoka.gradle.util.data.classifyProjects
import de.honoka.gradle.util.dsl.applier
import de.honoka.gradle.util.dsl.invoke
import de.honoka.gradle.util.dsl.libs
import de.honoka.gradle.util.dsl.projects

plugins {
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.lombok) apply false
    alias(libs.plugins.honoka.basic)
}

group = "de.honoka.qqrobot"
version = libs.versions.p.root.get()

val projects = classifyProjects {
    val webProject = "qqrobot-spring-boot-starter:web"
    jvm = subprojects - projects(webProject, "$webProject:admin", "$webProject:tester")
    library = jvm - projects("file-receiver")
}

projects.jvm {
    applier {
        java
        `java-library`
        `maven-publish`
        alias(libs.plugins.kotlin)
        alias(libs.plugins.kotlin.kapt)
        alias(libs.plugins.kotlin.lombok)
        alias(libs.plugins.honoka.basic)
    }

    group = rootProject.group

    honoka.basic {
        dependencies {
            kotlin()
            lombok()
        }

        configs {
            java(17, project in projects.library)
            kotlin()
            kapt()
        }
    }
}

honoka.basic {
    publishing {
        defineCheckVersionTask()
    }
}

//仅用于避免libs.versions.toml中产生version变量未使用的提示
libs.versions.d.kotlin.coroutines
libs.versions.d.lombok
libs.versions.d.spring.boot
