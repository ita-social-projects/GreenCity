package greencity.dto.habit;

import java.time.ZonedDateTime;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitAssignDto {
    private Long id;
    private Boolean acquired;
    private Boolean suspended;
    private ZonedDateTime createDateTime;
    private HabitDto habit;
    private Long userId;
    private Integer duration;
}
