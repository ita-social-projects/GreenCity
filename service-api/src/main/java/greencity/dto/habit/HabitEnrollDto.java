package greencity.dto.habit;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class HabitEnrollDto {
    private Long habitAssignId;
    private String habitName;
    private String habitDescription;
    private boolean isEnrolled;
}
