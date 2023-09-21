package greencity.dto.achievement;

import greencity.dto.achievementcategory.AchievementCategoryVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchievementVO {
    private Long id;
    private String title;

    private String name;
    private String nameEng;
    @NotEmpty
    private AchievementCategoryVO achievementCategory;

    @NotEmpty
    private Integer condition;
}
