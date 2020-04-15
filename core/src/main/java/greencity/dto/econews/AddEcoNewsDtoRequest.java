package greencity.dto.econews;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddEcoNewsDtoRequest {
    @NotEmpty
    private String title;

    @NotEmpty
    private String text;

    @NotEmpty
    private List<String> tags;
}
