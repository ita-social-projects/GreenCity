package greencity.dto.user;

import java.util.List;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UserProfileDtoRequest {
    private String firstName;
    private String city;
    private String userCredo;
    private List<String> socialNetworks;
    private Boolean showLocation;
    private Boolean showEcoPlace;
    private Boolean showShoppingList;
}
