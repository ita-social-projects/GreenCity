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
    private List<EcoNewsTranslationDto> translations;
    @NotEmpty
    private List<String> tags;
    @NotEmpty
    private String imagePath;
}
