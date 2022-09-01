package de.honoka.qqrobot.spring.boot.starter.component.util;

import de.honoka.qqrobot.spring.boot.starter.property.RobotBasicProperties;
import de.honoka.sdk.util.file.FileUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.Objects;

@Component
public class ImageCacheCleanTask {

    @Resource
    private RobotBasicProperties basicProperties;

    /**
     * 清理创建时间距现在超过5分钟的文件
     */
    @Scheduled(cron = "0 0 0/2 * * ?")
    public void cleanMessageImages() {
        File imgDir = new File(basicProperties.getMessageImagePath());
        for(File img : Objects.requireNonNull(imgDir.listFiles())) {
            try {
                Date createTime = FileUtils.getCreateTime(img);
                long timeInterval = System.currentTimeMillis() -
                        createTime.getTime();
                if(timeInterval > 5 * 60 * 1000)
                    img.delete();
            } catch(Throwable t) {
                //不报告此错误
            }
        }
    }
}