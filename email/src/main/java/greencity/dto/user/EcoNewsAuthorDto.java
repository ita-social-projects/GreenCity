package greencity.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class EcoNewsAuthorDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
}