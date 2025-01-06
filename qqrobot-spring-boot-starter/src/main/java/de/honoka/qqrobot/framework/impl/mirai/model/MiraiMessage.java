package de.honoka.qqrobot.framework.impl.mirai.model;

import lombok.Data;
import lombok.experimental.Accessors;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class MiraiMessage implements Closeable {

    private MessageChain messageChain;

    private List<ExternalResource> externalResources = new ArrayList<>();

    public MiraiMessage(MessageChain messageChain) {
        this.messageChain = messageChain;
    }
    
    @Override
    public void close() {
        for(ExternalResource res : externalResources) {
            try {
                res.close();
            } catch(Throwable t) {
                //ignore
            }
        }
    }
}
