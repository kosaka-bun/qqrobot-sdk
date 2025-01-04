package de.honoka.qqrobot.starter.component

import de.honoka.qqrobot.framework.api.RobotFramework
import de.honoka.qqrobot.starter.config.RobotBasicProperties
import de.honoka.sdk.util.kotlin.basic.log
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import kotlin.system.exitProcess

@Component
class RobotApplicationRunner(
    private val basicProperties: RobotBasicProperties,
    private val framework: RobotFramework
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
        if(!basicProperties.autoBoot) return
        runCatching {
            framework.boot()
        }.getOrElse {
            framework.stop()
            throw it
        }
    }
}
