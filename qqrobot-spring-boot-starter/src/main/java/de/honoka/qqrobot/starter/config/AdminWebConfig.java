package de.honoka.qqrobot.starter.config;

import de.honoka.qqrobot.starter.component.admin.AdminLoginInterceptor;
import de.honoka.qqrobot.starter.config.property.AdminProperties;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class AdminWebConfig implements WebMvcConfigurer {

    @Resource
    private AdminLoginInterceptor adminLoginInterceptor;

    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        String prefix = AdminProperties.WEB_PREFIX;
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns(prefix + "/**")
                .excludePathPatterns(Arrays.asList(
                        prefix + "/static/**",
                        prefix + "/api/login",
                        prefix + "/favicon.ico",
                        prefix + "/index.html",
                        prefix + "/"
                ));
    }

    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler(AdminProperties.WEB_PREFIX + "/**")
                .addResourceLocations("classpath:/web/admin/");
    }
}
