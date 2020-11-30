package greencity.dto.goal;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class GoalViewDto {
    private String id;
    private String content;
}
