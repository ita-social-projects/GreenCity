package greencity.dto.habit;

import greencity.dto.habittranslation.HabitTranslationDto;
import java.io.Serializable;
import java.util.List;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class HabitDto implements Serializable {
    private Integer defaultDuration;
    private HabitTranslationDto habitTranslation;
    private Long id;
    private String image;
    private List<String> tags;
}
