package greencity.dto.achievement;

import greencity.dto.achievementcategory.AchievementCategoryDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.EqualsAndHashCode;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
public class AchievementManagementDto extends AchievementPostDto {
    @NotNull
    @Min(1)
    private Long id;

    public AchievementManagementDto(Long id, String title, String name,
        String nameEng, AchievementCategoryDto achievementCategory, Integer condition) {
        super(title, name, nameEng, achievementCategory, condition);
        this.id = id;
    }
}
