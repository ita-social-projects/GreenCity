package greencity.mapping;

import greencity.dto.achievement.AchievementPostDto;
import greencity.dto.achievement.AchievementTranslationDto;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryDto;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.entity.Achievement;
import greencity.entity.AchievementCategory;
import greencity.entity.localization.AchievementTranslation;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AchievementPostDtoMapper extends AbstractConverter<AchievementPostDto, Achievement> {
    @Override
    protected Achievement convert(AchievementPostDto achievementPostDto) {
        AchievementCategoryDto achievementCategoryDto=achievementPostDto.getAchievementCategory();
       List<AchievementTranslation> achievementTranslations=achievementPostDto
               .getTranslations()
               .stream()
               .map(obj->AchievementTranslation.builder()
                       .achievement(Achievement)
                       .build());
        return Achievement.builder()
                .achievementCategory(AchievementCategory.builder().name(achievementCategoryDto.getName()).build())
                .translations(achievementPostDto.getTranslations().)
            .build();
    }
}
