package greencity.dto.habit;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class HabitEnrollDto {
    private Long habitId;
    private String habitName;
    private String habitDescription;
    private boolean isEnrolled;
}
