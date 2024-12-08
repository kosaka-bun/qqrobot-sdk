package de.honoka.qqrobot.starter.component.admin;

import de.honoka.qqrobot.starter.config.property.AdminProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminPageController {

    @RequestMapping(AdminProperties.WEB_PREFIX + "/")
    public String index() {
        return "forward:/admin/index.html";
    }

    @RequestMapping("/")
    public String root() {
        return "redirect:/admin/";
    }
}
