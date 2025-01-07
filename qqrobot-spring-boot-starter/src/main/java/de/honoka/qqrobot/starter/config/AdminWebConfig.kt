package de.honoka.qqrobot.starter.config

import de.honoka.qqrobot.starter.component.admin.AdminLoginInterceptor
import de.honoka.sdk.spring.starter.core.context.springBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@EnableConfigurationProperties(AdminProperties::class)
@Configuration
class AdminWebConfig : WebMvcConfigurer {
    
    private val excludePathPatterns = run {
        val prefix = AdminProperties.WEB_PREFIX
        listOf(
            "$prefix/static/**",
            "$prefix/api/login",
            "$prefix/favicon.ico",
            "$prefix/index.html",
            "$prefix/"
        )
    }
    
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.run {
            addInterceptor(AdminLoginInterceptor::class.springBean).run {
                addPathPatterns("${AdminProperties.WEB_PREFIX}/**")
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

@ConfigurationProperties("honoka.qqrobot.admin")
data class AdminProperties(
    
    /**
     * 后台管理界面的登录密码
     */
    var password: String = "123456"
) {
    
    companion object {
        
        const val WEB_PREFIX = "/admin"
    }
}
