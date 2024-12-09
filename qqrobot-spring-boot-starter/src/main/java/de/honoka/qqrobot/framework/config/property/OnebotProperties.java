package de.honoka.qqrobot.framework.config.property;

import de.honoka.sdk.util.file.FileUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Paths;

@ConfigurationProperties("honoka.qqrobot.framework.onebot")
@Data
public class OnebotProperties {
    
    private String host;
    
    private Integer httpPort;
    
    private Integer websocketPort;
    
    private Integer fileReceiverPort;
    
    /**
     * 定义缓存文件所存放的目录。<br>
     *
     * 发送图片或文件前，需要先将InputStream中的数据写出到文件中，才能被OneBot服务使用。
     */
    private String cachePath = Paths.get(FileUtils.getMainClasspath(), "cache").normalize().toString();
    
    public String getUrlPrefix() {
        return String.format("http://%s:%s", host, httpPort);
    }
    
    public String getWebsocketUrlPrefix() {
        return String.format("ws://%s:%s", host, websocketPort);
    }
    
    public String getFileReceiverUrlPrefix() {
        return String.format("http://%s:%s", host, fileReceiverPort);
    }
    
    public String getImagePath() {
        return Paths.get(cachePath, "image").toString();
    }
    
    public String getFileToUploadPath() {
        return Paths.get(cachePath, "fileToUpload").toString();
    }
}
