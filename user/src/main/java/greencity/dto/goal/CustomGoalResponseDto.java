package greencity.dto.goal;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CustomGoalResponseDto {
    @NonNull
    @Min(1)
    private Long id;
    @NotEmpty
    private String text;
}
