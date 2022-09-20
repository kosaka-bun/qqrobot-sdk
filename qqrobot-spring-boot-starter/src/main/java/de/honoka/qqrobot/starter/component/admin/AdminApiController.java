package de.honoka.qqrobot.starter.component.admin;

import com.google.gson.JsonObject;
import de.honoka.sdk.util.web.ApiResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;
import java.util.UUID;

@CrossOrigin
@RequestMapping("/admin/api")
@RestController
public class AdminApiController {

    public static final String LOGIN_USERNAME = "robot_admin";

    @Resource
    private AdminProperties adminProperties;

    @Resource
    private LoginInterceptor loginInterceptor;

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        if(!ObjectUtils.allNotNull(username, password)) {
            return ApiResponse.fail("用户名或密码不能为空");
        }
        //判断用户名密码
        boolean checkPassed = username.equals(LOGIN_USERNAME) &&
                password.equals(adminProperties.getPassword());
        //若未登录，且密码正确，添加登录状态
        //回应是否已登录，以及密码是否正确
        if(checkPassed) {
            String token = UUID.randomUUID().toString();
            loginInterceptor.putToken(username, token);
            JsonObject jo = new JsonObject();
            jo.addProperty("token", token);
            return ApiResponse.success(jo);
        } else {
            return ApiResponse.fail("用户名或密码不正确");
        }
    }

    @GetMapping("/user_info")
    public ApiResponse<?> userInfo() {
        JsonObject jo = new JsonObject();
        jo.addProperty("name", LOGIN_USERNAME);
        jo.addProperty("avatar", "./static/avatar.png");
        return ApiResponse.success(jo);
    }

    @PostMapping("/logout")
    public ApiResponse<?> logout() {
        return ApiResponse.success(null);
    }
}
