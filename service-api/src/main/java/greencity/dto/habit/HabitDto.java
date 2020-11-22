package greencity.dto.habit;

import greencity.dto.habittranslation.HabitTranslationDto;
import java.io.Serializable;
import java.util.List;

import greencity.dto.tag.TagVO;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class HabitDto implements Serializable {
    private Long id;
    private String image;
    private Integer defaultDuration;
    private HabitTranslationDto habitTranslation;
    private List<String> tags;
}
