package greencity.exception.handlers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response object with information of occur exception.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponse {
    private String message;
    @JsonIgnore
    private String timeStamp;
    @JsonIgnore
    private String trace;
    @JsonIgnore
    private String path;

    /**
     * Constructor with parameters.
     */
    public ExceptionResponse(Map<String, Object> errorAttributes) {
        this.setPath((String) errorAttributes.get("path"));
        this.setMessage((String) errorAttributes.get("message"));
        this.setTimeStamp(errorAttributes.get("timestamp").toString());
        this.setTrace((String) errorAttributes.get("trace"));
    }
}
