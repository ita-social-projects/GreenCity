package greencity.message;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class VerifyEmailMessage implements Serializable {
    private final Long id;
    private final String name;
    private final String email;
    private final String token;
    private final String language;
}
