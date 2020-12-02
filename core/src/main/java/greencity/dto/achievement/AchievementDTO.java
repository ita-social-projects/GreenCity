package greencity.dto.achievement;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementDTO {
    @NotNull
    @Min(value = 1, message = "Achievement id must be a positive number")
    private Long id;

    @NotEmpty(message = "Achievement text must not be null")
    private String title;

    @NotEmpty(message = "Description of achievement must not be null")
    private String description;

    @NotEmpty(message = "Message of achievement must not be null")
    private String message;
}
