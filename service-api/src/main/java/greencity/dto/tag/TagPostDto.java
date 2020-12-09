package greencity.dto.tag;

import greencity.enums.TagType;
import lombok.*;

import javax.validation.Valid;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class TagPostDto {
    @Valid
    private TagType type;

    @Valid
    private List<TagTranslationDto> tagTranslations;
}
