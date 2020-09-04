package greencity.dto.user;

import greencity.dto.socialnetwork.SocialNetworkResponseDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UserProfileDtoResponse {
    private String profilePicturePath;
    private String firstName;
    private String city;
    private String userCredo;
    private List<SocialNetworkResponseDTO> socialNetworks = new ArrayList<>();
    private Boolean showLocation;
    private Boolean showEcoPlace;
    private Boolean showShoppingList;
    private Float rating;
}
