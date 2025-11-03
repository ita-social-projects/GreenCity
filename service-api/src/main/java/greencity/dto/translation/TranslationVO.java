package greencity.dto.translation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
public class TranslationVO {
    private Long id;

    private String languageCode;

    private String content;
}
