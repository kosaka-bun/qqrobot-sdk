package de.honoka.qqrobot.filereceiver.common

import de.honoka.sdk.util.kotlin.various.log
import de.honoka.sdk.util.web.ApiResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ResponseBody
@ControllerAdvice
class AllExceptionHandler {

    @ExceptionHandler
    fun handleAll(t: Throwable): ApiResponse<*> {
        log.error("", t)
        return ApiResponse.fail(t.message)
    }
}
