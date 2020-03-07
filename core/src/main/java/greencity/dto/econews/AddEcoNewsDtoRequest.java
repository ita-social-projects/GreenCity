package greencity.dto.econews;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddEcoNewsDtoRequest {
    @NotEmpty
    private List<EcoNewsTranslationDto> translations;
    @NotEmpty
    private String imagePath;
}
