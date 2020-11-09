package greencity.dto.user;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitIdRequestDto {
    @Min(1)
    @NotNull
    private Long id;
}
