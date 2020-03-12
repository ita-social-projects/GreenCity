package greencity.dto.econews;

import greencity.dto.tag.TagDto;
import greencity.dto.user.EcoNewsAuthorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

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
