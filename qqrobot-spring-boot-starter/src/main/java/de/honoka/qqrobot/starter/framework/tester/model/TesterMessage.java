package de.honoka.qqrobot.starter.framework.tester.model;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TesterMessage {

    private String id;

    private String type;

    private JsonObject data = new JsonObject();

    public TesterMessage(String id) {
        this.id = id;
    }
}
