package greencity.dto.achievement;

import greencity.dto.achievementcategory.AchievementCategoryDto;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementPostDto {

    @NotEmpty
    private String title;

    @NotEmpty
    private String description;

    @NotEmpty
    private String message;

    private AchievementCategoryDto achievementCategory;

    private Integer condition;
}
