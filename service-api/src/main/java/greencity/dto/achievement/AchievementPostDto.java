package greencity.dto.achievement;

import greencity.dto.achievementcategory.AchievementCategoryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AchievementPostDto {
    private String title;
    private String name;
    private String nameEng;
    private AchievementCategoryDto achievementCategory;
    private Integer condition;
}
