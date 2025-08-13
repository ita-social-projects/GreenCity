package greencity.enums;

import greencity.constant.OpenAIRequest;
import java.util.Map;

public enum OpenAIResponseFormat {
    TEXT {
        @Override
        public Map<String, Object> getFormat() {
            return Map.of("type", "text");
        }
    },
    JSON_SCHEMA {
        @Override
        public Map<String, Object> getFormat() {
            return Map.of("type", "json_schema",
                "json_schema", OpenAIRequest.JSON_SCHEMA);
        }
    };

    public abstract Map<String, Object> getFormat();
}
