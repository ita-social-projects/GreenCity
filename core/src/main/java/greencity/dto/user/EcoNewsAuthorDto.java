package greencity.dto.user;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EcoNewsAuthorDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
}

