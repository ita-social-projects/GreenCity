package greencity.dto.achievement;

import greencity.dto.achievementcategory.AchievementCategoryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AchievementPostDto {
    private List<AchievementTranslationVO> translations;

    private AchievementCategoryDto achievementCategory;

    private Integer condition;
}
