package greencity.dto.tag;

import greencity.annotations.LanguageTranslationConstraint;
import greencity.enums.TagType;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class TagPostDto {
    @NotNull
    private TagType type;

    @Valid
    @LanguageTranslationConstraint
    private List<TagTranslationDto> tagTranslations;
}
