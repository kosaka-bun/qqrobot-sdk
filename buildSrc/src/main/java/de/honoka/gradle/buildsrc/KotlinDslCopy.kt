package de.honoka.gradle.buildsrc

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.publish.PublishingExtension

fun Project.publishing(configure: Action<PublishingExtension>) {
    extensions.configure("publishing", configure)
}

fun DependencyHandler.implementation(dependencyNotation: Any): Dependency? = run {
    add("implementation", dependencyNotation)
}