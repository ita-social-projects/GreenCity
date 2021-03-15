package greencity.dto.habitfact;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitFactViewDto {
    private String id;
    private String habitId;
    private String content;
}
