package greencity.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.io.Serializable;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeneralEmailMessage implements Serializable {
    private String email;
    private String subject;
    private String message;
}
