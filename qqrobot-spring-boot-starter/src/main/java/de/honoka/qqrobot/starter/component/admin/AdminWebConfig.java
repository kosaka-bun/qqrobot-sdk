package de.honoka.qqrobot.starter.component.admin;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.Arrays;

@Configuration
public class AdminWebConfig implements WebMvcConfigurer {

    @Resource
    private LoginInterceptor loginInterceptor;

    @Resource
    private AdminProperties adminProperties;

    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns(adminProperties.getWebPrefix() + "/**")
                .excludePathPatterns(Arrays.asList(
                        "/admin/static/**",
                        "/admin/api/login",
                        "/admin/favicon.ico",
                        "/admin/index.html",
                        "/admin/"
                ));
    }

    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler(adminProperties
                .getWebPrefix() + "/**"
        ).addResourceLocations("classpath:/web/admin/");
    }
}
