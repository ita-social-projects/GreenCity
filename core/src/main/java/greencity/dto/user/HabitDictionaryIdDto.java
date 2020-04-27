package greencity.dto.user;

import javax.validation.constraints.Min;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class HabitDictionaryIdDto {
    @Min(1)
    private Long id;
}