package greencity.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PlaceAuthorDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
