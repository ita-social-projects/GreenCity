package greencity.dto.tag;

import greencity.dto.language.LanguageTranslationDTO;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Size;

@Getter
@Setter
public class TagTranslationDto extends LanguageTranslationDTO {

    @Valid
    @Size(min = 1, max = 20)
    private String name;
}
