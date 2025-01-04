package de.honoka.qqrobot.starter.config

import de.honoka.qqrobot.starter.component.admin.AdminLoginInterceptor
import de.honoka.sdk.spring.starter.core.context.springBean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class AdminWebConfig : WebMvcConfigurer {
    
    override fun addInterceptors(registry: InterceptorRegistry) {
        val prefix = AdminProperties.WEB_PREFIX
        registry.run {
            addInterceptor(AdminLoginInterceptor::class.springBean).run {
                addPathPatterns("$prefix/**")
                val excludePathPatterns = listOf(
                    "$prefix/static/**",
                    "$prefix/api/login",
                    "$prefix/favicon.ico",
                    "$prefix/index.html",
                    "$prefix/"
                )
                excludePathPatterns(excludePathPatterns)
            }
        }
    }
    
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.run {
            addResourceHandler("${AdminProperties.WEB_PREFIX}/**").run {
                addResourceLocations("classpath:/web/admin/")
            }
        }
    }
}
