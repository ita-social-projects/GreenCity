package greencity.dto.habit;

import java.time.ZonedDateTime;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitAssignDto {
    @NotNull
    @Min(1)
    private Long id;
    @NotEmpty
    private Boolean acquired;
    @NotEmpty
    private Boolean suspended;
    @NotEmpty
    private ZonedDateTime createDateTime;
    @NotNull
    @Min(1)
    private Long habitId;
}
