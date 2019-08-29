package greencity.dto.favoritePlace;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class FavoritePlaceDto {
    @NotBlank
    @Length(max = 30)
    private String name;

    private Long placeId ;

    private String userEmail;
}
