package greencity.dto.econews;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AddEcoNewsDtoRequest {
    @NotEmpty
    private String title;

    @NotEmpty
    private String text;

    @NotEmpty
    private List<String> tags;
}
