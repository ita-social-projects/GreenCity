package greencity.dto.achievement;

import greencity.dto.achievementcategory.AchievementCategoryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class AchievementPostDto {
    private String title;
    private String nameUk;
    private String nameEn;
    private AchievementCategoryDto achievementCategory;
    private Integer condition;
}
