package greencity.dto.achievement;

import greencity.dto.achievementcategory.AchievementCategoryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementPostDto {

    private List<AchievementTranslationVO> translations;

    private AchievementCategoryDto achievementCategory;

    private Integer condition;
}
