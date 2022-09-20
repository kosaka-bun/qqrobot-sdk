package de.honoka.qqrobot.starter.component.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminPageController {

    @RequestMapping("/admin/")
    public String main() {
        return "forward:/admin/index.html";
    }

    @RequestMapping("/")
    public String root() {
        return "redirect:/admin/";
    }
}
