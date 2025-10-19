package de.honoka.qqrobot.starter

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@ComponentScan(
    "de.honoka.qqrobot.starter.config",
    "de.honoka.qqrobot.starter.core",
    "de.honoka.qqrobot.starter.framework.config",
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator::class
)
@AutoConfiguration
class QqRobotStarter
