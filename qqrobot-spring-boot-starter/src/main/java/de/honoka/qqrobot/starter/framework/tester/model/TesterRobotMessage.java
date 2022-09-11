package de.honoka.qqrobot.starter.framework.tester.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.honoka.qqrobot.starter.framework.tester.server.TesterServer;
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

    @Data
    @AllArgsConstructor
    public static class Part {

        private String type;

        private String content;

        private JsonObject extras;

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

    public static TesterRobotMessage of(JsonArray content) {
        TesterRobotMessage message = new TesterRobotMessage();
        for(JsonElement partJson : content) {
            Part part = TesterServer.gson.fromJson(partJson, Part.class);
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

    public JsonArray toJsonArray() {
        return TesterServer.gson.toJsonTree(parts).getAsJsonArray();
    }
}
