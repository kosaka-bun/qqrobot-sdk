package de.honoka.qqrobot.starter.component.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class AdminLoginInterceptor implements HandlerInterceptor {

    private final Map<String, String> tokenMap = new HashMap<>();

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public void putToken(String username, String token) {
        tokenMap.put(username, token);
    }

    /**
     * 在处理方法之前执行，一般用来做一些准备工作：比如日志，权限检查
     * 如果返回false，表示被拦截，将不会执行处理方法
     * 返回true，继续执行处理方法
     */
    @Override
    @SneakyThrows
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {
        //只拦截admin所部署的路径下的请求
        if(!request.getRequestURI().startsWith(contextPath +
                AdminProperties.WEB_PREFIX)) {
            return true;
        }
        if(request.getMethod().toLowerCase(Locale.ROOT).equals("options")) {
            return true;
        }
        String token = request.getHeader("X-Token");
        if(!tokenMap.containsValue(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().println("请登录");
            return false;
        }
        return true;
    }

    /**
     * 在处理方法执行之后，在渲染视图执行之前执行，一般用来做一些清理工作
     */
    @Override
    public void postHandle(@NotNull HttpServletRequest request,
                           @NotNull HttpServletResponse response,
                           @NotNull Object handler, ModelAndView modelAndView) {

    }

    /**
     * 在视图渲染后执行，一般用来释放资源
     */
    @Override
    public void afterCompletion(@NotNull HttpServletRequest request,
                                @NotNull HttpServletResponse response,
                                @NotNull Object handler, Exception ex) {

    }
}
