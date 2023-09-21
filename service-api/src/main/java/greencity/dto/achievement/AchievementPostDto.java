package greencity.dto.achievement;

import greencity.dto.achievementcategory.AchievementCategoryDto;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AchievementPostDto {
private String title;
    private String name;
    private String nameEng;

    private AchievementCategoryDto achievementCategory;

    private Integer condition;
}
