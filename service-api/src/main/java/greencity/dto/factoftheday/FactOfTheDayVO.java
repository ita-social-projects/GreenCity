package greencity.dto.factoftheday;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"factOfTheDayTranslations","createDate"})

public class FactOfTheDayVO {
    private Long id;

    private String name;

    private List<FactOfTheDayTranslationVO> factOfTheDayTranslations;

    private ZonedDateTime createDate;

}
