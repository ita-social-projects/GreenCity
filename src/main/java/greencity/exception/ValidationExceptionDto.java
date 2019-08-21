package greencity.exception;

import java.io.Serializable;

import lombok.Data;
import org.springframework.validation.FieldError;

@Data
public class ValidationExceptionDto implements Serializable {
    private String name;
    private String message;

    public ValidationExceptionDto(FieldError error) {
        this.name = error.getField();
        this.message = error.getDefaultMessage();
    }
}
