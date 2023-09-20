package greencity.mapping;

import greencity.dto.achievement.AchievementTranslationDto;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.language.LanguageVO;
import greencity.entity.Achievement;
import java.util.ArrayList;
import java.util.List;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class AchievementVOMapper extends AbstractConverter<Achievement, AchievementVO> {
    @Override
    protected AchievementVO convert(Achievement achievement) {
        List<AchievementTranslationDto> list = new ArrayList<>();
        achievement.getTranslations()
            .forEach(achievementTranslation -> list.add(AchievementTranslationDto.builder()
                .id(achievementTranslation.getId())
                .name(achievementTranslation.getName())
                .nameEng(achievementTranslation.getNameEng())
                .build()));
        return AchievementVO.builder()
            .id(achievement.getId())
            .translations(list)
            .achievementCategory(AchievementCategoryVO.builder()
                .id(achievement.getAchievementCategory().getId())
                .name(achievement.getAchievementCategory().getName())
                .build())
            .build();
    }
}
