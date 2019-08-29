package greencity.dto.favoritePlace;

import greencity.dto.place.PlaceIdDto;
import greencity.dto.user.UserEmailDto;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class FavoritePlaceDto {
    @NotBlank
    @Length(max = 30)
    private String name;

    private PlaceIdDto place;

    private UserEmailDto user;
}
