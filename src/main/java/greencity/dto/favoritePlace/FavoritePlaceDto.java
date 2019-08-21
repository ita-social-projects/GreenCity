package greencity.dto.favoritePlace;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
public class FavoritePlaceDto {
    @NotBlank
    @Length(max = 30)
    private String name;
    @NotNull
    private long placeId;
    @NotNull
    private long userId;
}
