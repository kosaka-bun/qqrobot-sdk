package de.honoka.qqrobot.filereceiver.common

import de.honoka.sdk.util.framework.web.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ResponseBody
@ControllerAdvice
class AllExceptionHandler {
    
    companion object {
        
        private val log = LoggerFactory.getLogger(AllExceptionHandler::class.java)
    }
    
    @ExceptionHandler
    fun handleAll(t: Throwable): ApiResponse<*> {
        log.error("", t)
        return ApiResponse.fail(t.message)
    }
}