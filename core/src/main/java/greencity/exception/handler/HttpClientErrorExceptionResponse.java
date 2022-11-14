package greencity.exception.handler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;
import lombok.Value;

@Value
public class HttpClientErrorExceptionResponse {
    String message;
    @JsonIgnore
    String timeStamp;
    @JsonIgnore
    String trace;
    @JsonIgnore
    String path;

    /**
     * Constructor that creates an instance from the Map with error attributes and
     * String message.
     */
    public HttpClientErrorExceptionResponse(Map<String, Object> errorAttributes, String message) {
        this.path = errorAttributes.get("path").toString();
        this.message = message;
        this.timeStamp = errorAttributes.get("timestamp").toString();
        this.trace = errorAttributes.get("trace").toString();
    }
}
