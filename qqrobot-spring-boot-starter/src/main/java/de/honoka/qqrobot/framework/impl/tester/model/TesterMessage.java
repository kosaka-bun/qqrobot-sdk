package de.honoka.qqrobot.framework.impl.tester.model;

import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TesterMessage {

    private String id;

    private String type;

    private JSONObject data = new JSONObject();

    public TesterMessage(String id) {
        this.id = id;
    }
}
