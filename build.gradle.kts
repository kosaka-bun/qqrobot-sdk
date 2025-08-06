import de.honoka.gradle.buildsrc.MavenPublish.defineCheckVersionOfProjectsTask
import de.honoka.gradle.buildsrc.kotlin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.charset.StandardCharsets

plugins {
    java
    `java-library`
    `maven-publish`
    alias(libs.plugins.dependency.management)
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.kotlin.kapt)
    /*
     * Lombok Kotlin compiler plugin is an experimental feature.
     * See: https://kotlinlang.org/docs/components-stability.html.
     */
    alias(libs.plugins.kotlin.lombok) apply false
}

group = "de.honoka.qqrobot"
version = libs.versions.root.get()

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "org.jetbrains.kotlin.plugin.lombok")
    
    val libs = rootProject.libs

    group = rootProject.group

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = sourceCompatibility
        withSourcesJar()
    }
    
    dependencyManagement {
        imports {
            mavenBom(libs.kotlin.bom.get().toString())
        }
    }

    dependencies {
        kotlin(project)
        //仅用于避免libs.versions.toml中产生version变量未使用的提示
        libs.versions.kotlin.coroutines
        libs.lombok.let {
            compileOnly(it)
            annotationProcessor(it)
            testCompileOnly(it)
            testAnnotationProcessor(it)
        }
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    }

    tasks {
        withType<JavaCompile> {
            options.run {
                encoding = StandardCharsets.UTF_8.name()
                val compilerArgs = compilerArgs as MutableCollection<String>
                compilerArgs += listOf(
                    "-parameters"
                )
            }
        }
        
        withType<KotlinCompile> {
            compilerOptions {
                jvmTarget.set(JvmTarget.fromTarget(java.sourceCompatibility.toString()))
                freeCompilerArgs.addAll("-Xjsr305=strict", "-Xjvm-default=all")
            }
        }

        withType<Test> {
            useJUnitPlatform()
        }
    }

    publishing {
        repositories {
            mavenLocal()
        }
    }
    
    kapt {
        keepJavacAnnotationProcessors = true
    }
}

defineCheckVersionOfProjectsTask()
