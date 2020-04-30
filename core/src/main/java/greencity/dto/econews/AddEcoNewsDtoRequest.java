package greencity.dto.econews;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
