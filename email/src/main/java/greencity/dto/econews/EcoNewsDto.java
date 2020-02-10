package greencity.dto.econews;

import java.time.ZonedDateTime;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EcoNewsDto {
    @NotNull
    @Min(0)
    private Long id;
    @NotEmpty
    private String title;
    @NotEmpty
    private ZonedDateTime creationDate;
    @NotEmpty
    private String text;
    @NotEmpty
    private String imagePath;
}
