package greencity.dto.econews;

import greencity.dto.tag.TagDto;
import greencity.dto.user.EcoNewsAuthorDto;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddEcoNewsDtoRequest {
    @NotEmpty
    private List<EcoNewsTranslationDto> translations;
    @NotEmpty
    private List<TagDto> tags;

    private EcoNewsAuthorDto author;
    @NotEmpty
    private String imagePath;
}
