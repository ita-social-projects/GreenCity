package greencity.dto.tag;

import greencity.dto.language.LanguageDTO;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class TagTranslationDto {
    @Valid
    @NotNull
    private LanguageDTO language;

    @Size(max = 20)
    private String name;
}
