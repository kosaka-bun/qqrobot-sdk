package de.honoka.qqrobot.framework.impl.tester.server;

import de.honoka.qqrobot.framework.impl.tester.config.TesterProperties;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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

    @SneakyThrows
    @RequestMapping("/image")
    public synchronized void getImage(@RequestParam String name,
                                      HttpServletResponse response) {
        response.setContentType("image/png");
        OutputStream os = response.getOutputStream();
        String path = testerProperties.getImagePath() + File.separator +
                name + ".png";
        try(InputStream is = Files.newInputStream(Paths.get(path))) {
            byte[] bytes = IOUtils.toByteArray(is);
            os.write(bytes);
        }
    }
}
