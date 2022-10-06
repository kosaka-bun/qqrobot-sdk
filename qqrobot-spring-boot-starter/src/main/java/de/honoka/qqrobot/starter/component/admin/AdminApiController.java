package de.honoka.qqrobot.starter.component.admin;

import com.google.gson.JsonObject;
import de.honoka.qqrobot.framework.Framework;
import de.honoka.qqrobot.starter.RobotBasicProperties;
import de.honoka.qqrobot.starter.common.RobotBeanHolder;
import de.honoka.qqrobot.starter.component.RobotConsoleWindow;
import de.honoka.qqrobot.starter.component.logger.dao.ExceptionRecordDao;
import de.honoka.qqrobot.starter.component.logger.dao.UsageLogDao;
import de.honoka.qqrobot.starter.component.logger.entity.ExceptionRecord;
import de.honoka.qqrobot.starter.component.logger.entity.UsageLog;
import de.honoka.sdk.util.code.ActionUtils;
import de.honoka.sdk.util.framework.web.ApiResponse;
import de.honoka.sdk.util.system.SystemInfoBean;
import de.honoka.sdk.util.system.gui.ConsoleWindow;
import de.honoka.sdk.util.text.TextUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@CrossOrigin
@RequestMapping(AdminProperties.WEB_PREFIX + "/api")
@RestController
public class AdminApiController {

    public static final String LOGIN_USERNAME = "robot_admin";

    private static final int
            //使用记录的每页记录条数
            USAGE_LOG_PAGE_SIZE = 20,
            //异常信息最大显示条数
            EXCEPTION_RECORD_MAX_SIZE = 10;

    //region components

    @Resource
    private AdminProperties adminProperties;

    @Resource
    private AdminLoginInterceptor adminLoginInterceptor;

    @Resource
    private UsageLogDao usageLogDao;

    @Resource
    private ExceptionRecordDao exceptionRecordDao;

    @Resource
    private RobotBasicProperties basicProperties;

    @Lazy
    @Resource
    private Framework<?> framework;

    //endregion

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
            adminLoginInterceptor.putToken(username, token);
            Map<String, Object> res = new HashMap<>();
            res.put("token", token);
            return ApiResponse.success(res);
        } else {
            return ApiResponse.fail("用户名或密码不正确");
        }
    }

    @GetMapping("/user_info")
    public ApiResponse<?> userInfo() {
        Map<String, Object> res = new HashMap<>();
        res.put("name", LOGIN_USERNAME);
        res.put("avatar", "./static/avatar.png");
        return ApiResponse.success(res);
    }

    @PostMapping("/logout")
    public ApiResponse<?> logout() {
        return ApiResponse.success(null);
    }

    @GetMapping("/main")
    public ApiResponse<?> mainInfo() {
        Map<String, Object> data = new HashMap<>();
        data.put("system_info", new SystemInfoBean());
        data.put("will_send_test_message_on_relogin", basicProperties
                .getSendTestMessageOnRelogin());
        data.put("will_resend_on_send_failed", basicProperties
                .getResendOnSendFailed());
        return ApiResponse.success(null, data);
    }

    @RequestMapping("/switch/resend_on_failed")
    public ApiResponse<?> switchWillResendOnSendFailed() {
        basicProperties.setResendOnSendFailed(!basicProperties
                .getResendOnSendFailed());
        return ApiResponse.success(null);
    }

    @RequestMapping("/switch/send_test_message")
    public ApiResponse<?> switchWillSendTestMessageOnRelogin() {
        basicProperties.setSendTestMessageOnRelogin(!basicProperties
                .getSendTestMessageOnRelogin());
        return ApiResponse.success(null);
    }

    @RequestMapping("/exception")
    public String getException() {
        List<ExceptionRecord> list = exceptionRecordDao.readException(
                EXCEPTION_RECORD_MAX_SIZE);
        return RobotBeanHolder.gson.toJson(ApiResponse.success(list));
    }

    @RequestMapping("/usage_log")
    public String getUsageLog(
            @RequestParam(required = false, defaultValue = "1") int page) {
        int maxPage;
        //获取信息
        //计算最大页数
        int count = usageLogDao.getCount();
        maxPage = count / USAGE_LOG_PAGE_SIZE;
        if(count % USAGE_LOG_PAGE_SIZE > 0) maxPage++;
        if(maxPage > 10) maxPage = 10;
        //修正不正确的当前页号
        if(page > maxPage) page = maxPage;    //此处page可能会被赋值为0
        if(page <= 0) page = 1;
        //获取使用记录
        List<UsageLog> logs = usageLogDao.selectPage(page,
                USAGE_LOG_PAGE_SIZE);
        //组装Json
        Map<String, Object> result = new HashMap<>();
        result.put("page", page);
        result.put("maxPage", maxPage);
        result.put("PAGE_SIZE", USAGE_LOG_PAGE_SIZE);
        result.put("list", logs);
        return RobotBeanHolder.gson.toJson(ApiResponse.success(result));
    }

    @RequestMapping("/console")
    public String getConsole() {
        ConsoleWindow window = RobotConsoleWindow.getConsole();
        String content;
        if(window != null) {
            content = window.getText();
        } else {
            content = "<div style=\"color: white;\">" +
                    "应用未开启控制台窗口" +
                    "</div>";
        }
        JsonObject result = new JsonObject();
        result.addProperty("status", true);
        result.addProperty("data", content);
        return RobotBeanHolder.gson.toJson(result);
    }

    @RequestMapping("/action/send_test_message")
    public ApiResponse<?> sendTestMessage() {
        ActionUtils.doAction("发送测试消息", () -> {
            framework.sendGroupMsg(
                    basicProperties.getDevelopingGroup(),
                    TextUtils.getSimpleDateFormat().format(new Date()) +
                            "\n测试消息"
            );
        });
        return ApiResponse.success(null, null);
    }

    @RequestMapping("/action/relogin")
    public ApiResponse<?> relogin() {
        ActionUtils.doAction("重新登录", framework::reboot);
        return ApiResponse.success(null, null);
    }
}
