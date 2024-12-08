package de.honoka.qqrobot.starter.component

import de.honoka.qqrobot.framework.api.Framework
import de.honoka.qqrobot.starter.config.property.RobotBasicProperties
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class RobotApplicationRunner(
    private val basicProperties: RobotBasicProperties,
    private val framework: Framework<*>
) : ApplicationRunner {
    
    override fun run(args: ApplicationArguments) {
        if(basicProperties.isAutoBoot) framework.boot()
    }
}