package greencity.dto.favoriteplace;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class FavoritePlaceDto {
    @NotBlank
    @Length(max = 30)
    private String name;
    private Long placeId;
}
