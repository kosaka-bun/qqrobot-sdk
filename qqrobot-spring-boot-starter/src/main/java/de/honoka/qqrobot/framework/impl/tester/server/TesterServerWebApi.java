package de.honoka.qqrobot.framework.impl.tester.server;

import cn.hutool.core.io.IoUtil;
import de.honoka.qqrobot.framework.config.property.TesterProperties;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@RequestMapping(TesterProperties.WEB_PREFIX)
@RestController
public class TesterServerWebApi {

    @Resource
    private TesterProperties testerProperties;

    @RequestMapping("/image")
    @SneakyThrows
    public synchronized void getImage(@RequestParam String name, HttpServletResponse response) {
        response.setContentType("image/png");
        OutputStream os = response.getOutputStream();
        String path = testerProperties.getImagePath() + File.separator +
                name + ".png";
        try(InputStream is = Files.newInputStream(Paths.get(path))) {
            byte[] bytes = IoUtil.readBytes(is, false);
            os.write(bytes);
        }
    }
}
