package greencity.dto.language;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class LanguageVO {
    private Long id;
    private String code;
}
