package de.honoka.qqrobot.framework.impl.onebot.config;

import de.honoka.sdk.util.file.FileUtils;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Paths;

@ConfigurationProperties("honoka.qqrobot.onebot")
@Data
@Accessors(chain = true)
public class OnebotProperties {
    
    private String host;
    
    private Integer httpPort;
    
    private Integer websocketPort;
    
    /**
     * 定义缓存文件所存放的目录。<br>
     *
     * 发送图片或文件前，需要先将InputStream中的数据写出到文件中，才能被OneBot服务使用。
     */
    private String cachePath = Paths.get(FileUtils.getMainClasspath(), "cache").normalize().toString();
}
