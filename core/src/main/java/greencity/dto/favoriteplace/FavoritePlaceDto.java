package greencity.dto.favoriteplace;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoritePlaceDto {
    @NotBlank
    @Length(max = 30)
    private String name;
    private Long placeId;
}
