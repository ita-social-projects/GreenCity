package greencity.dto.user;

import javax.validation.constraints.Min;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitIdRequestDto {
    @Min(1)
    private Long id;
}

