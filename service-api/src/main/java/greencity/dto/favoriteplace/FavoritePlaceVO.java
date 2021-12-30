package greencity.dto.favoriteplace;

import greencity.dto.place.PlaceVO;
import greencity.dto.user.UserVO;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = "name")
public class FavoritePlaceVO {
    private Long id;

    private String name;

    private UserVO user;

    private PlaceVO place;
}
