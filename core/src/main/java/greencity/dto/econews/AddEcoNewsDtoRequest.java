package greencity.dto.econews;

import greencity.dto.user.EcoNewsAuthorDto;
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

    private EcoNewsAuthorDto author;
    @NotEmpty
    private String imagePath;
}
