package greencity.exception;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExceptionResponse {
    private String message;
    @JsonIgnore private String timeStamp;
    @JsonIgnore private String trace;
    @JsonIgnore private String path;

    public ExceptionResponse(Map<String, Object> errorAttributes) {
        this.setPath((String) errorAttributes.get("path"));
        this.setMessage((String) errorAttributes.get("message"));
        this.setTimeStamp(errorAttributes.get("timestamp").toString());
        this.setTrace((String) errorAttributes.get("trace"));
    }
}
