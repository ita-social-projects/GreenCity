package greencity.dto.econews;

import greencity.dto.language.LanguageRequestDto;
import greencity.dto.tag.TagDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetEcoNewsDto {
    @NotNull
    private LanguageRequestDto language;
    @NotEmpty
    private List<TagDto> tags;

}
