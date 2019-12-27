package greencity.dto.econews;

import java.time.ZonedDateTime;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddEcoNewsDtoResponse {
    @NotEmpty
    private String title;
    @NotEmpty
    private String text;
    @NotEmpty
    private ZonedDateTime creationDate;
    @NotEmpty
    private String imagePath;
}
