package greencity.message;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduledEmailMessage implements Serializable {
    @NotEmpty(message = "Username cannot be empty")
    private String username;
    @NotEmpty(message = "Email cannot be empty")
    private String email;
    @NotEmpty(message = "Link cannot be empty")
    private String baseLink;
    @NotEmpty(message = "Subject cannot be empty")
    private String subject;
    @NotEmpty(message = "Body cannot be empty")
    private String body;
    @NotEmpty(message = "Language cannot be empty")
    private String language;
}
