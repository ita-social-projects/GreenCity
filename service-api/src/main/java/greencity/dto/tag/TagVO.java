package greencity.dto.tag;

import greencity.dto.econews.EcoNewsVO;
import greencity.dto.habit.HabitVO;
import greencity.dto.tipsandtricks.TipsAndTricksVO;
import java.util.List;
import java.util.Set;

import greencity.enums.TagType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class TagVO {
    private Long id;
    private TagType type;
    private List<TagTranslationVO> tagTranslations;
    private List<EcoNewsVO> ecoNews;
    private List<TipsAndTricksVO> tipsAndTricks;
    private Set<HabitVO> habits;
}
