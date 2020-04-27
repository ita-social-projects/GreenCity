package greencity.dto.favoriteplace;

import javax.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class FavoritePlaceDto {
    @NotBlank
    @Length(max = 30)
    private String name;
    private Long placeId;
}
