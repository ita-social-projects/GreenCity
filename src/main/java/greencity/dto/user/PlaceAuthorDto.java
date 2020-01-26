package greencity.dto.user;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PlaceAuthorDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
