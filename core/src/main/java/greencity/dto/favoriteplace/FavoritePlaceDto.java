package greencity.dto.favoriteplace;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoritePlaceDto {
    @NotBlank
    @Length(max = 30)
    private String name;
    private Long placeId;
}
