package greencity.dto.advice;

import greencity.dto.language.LanguageGeneralDto;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class AdviceTranslationGeneralDto {
    private Long id;

    private LanguageGeneralDto language;

    private AdviceGeneralDto advice;

    private String content;
}
