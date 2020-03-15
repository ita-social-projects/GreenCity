package greencity.dto.econews;

import greencity.dto.language.LanguageRequestDto;
import greencity.dto.tag.TagDto;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteriaEcoNewsDto {
    @NotNull
    private LanguageRequestDto language;
    @NotEmpty
    private List<TagDto> tags;
}
