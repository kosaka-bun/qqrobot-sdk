package de.honoka.qqrobot.framework.impl.tester.model;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class TesterRobotMessage {

    private List<Part> parts = new ArrayList<>();

    public TesterRobotMessage() {}
    
    @AllArgsConstructor
    @Data
    public static class Part {

        private String type;

        private String content;

        private JSONObject extras;

        public Part(String type, String content) {
            this.type = type;
            this.content = content;
        }
    }

    public interface PartType {

        String TEXT = "text";

        String AT = "at";

        String IMAGE = "image";
    }

    public static TesterRobotMessage of(JSONArray content) {
        TesterRobotMessage message = new TesterRobotMessage();
        for(Object partJson : content) {
            Part part = ((JSONObject) partJson).toBean(Part.class);
            message.parts.add(part);
        }
        return message;
    }

    public TesterRobotMessage add(String type, String content) {
        parts.add(new Part(type, content));
        return this;
    }

    public TesterRobotMessage add(Part part) {
        parts.add(part);
        return this;
    }

    public JSONArray toJsonArray() {
        return new JSONArray(parts);
    }
}
