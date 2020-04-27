package greencity.dto.goal;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CustomGoalSaveRequestDto {
    @NonNull
    private String text;
}
