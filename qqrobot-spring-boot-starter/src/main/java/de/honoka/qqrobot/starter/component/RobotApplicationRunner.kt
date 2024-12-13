package de.honoka.qqrobot.starter.component

import de.honoka.qqrobot.framework.api.Framework
import de.honoka.qqrobot.starter.config.property.RobotBasicProperties
import de.honoka.sdk.util.kotlin.code.log
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import kotlin.system.exitProcess

@Component
class RobotApplicationRunner(
    private val basicProperties: RobotBasicProperties,
    private val framework: Framework<*>
) : ApplicationRunner {
    
    override fun run(args: ApplicationArguments) {
        runCatching {
            bootFramework()
        }.getOrElse {
            log.error("", it)
            exitProcess(1)
        }
    }
    
    fun bootFramework() {
        if(!basicProperties.isAutoBoot) return
        runCatching {
            framework.boot()
        }.getOrElse {
            framework.stop()
            throw it
        }
    }
}