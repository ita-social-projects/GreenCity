package greencity.mapping;

import greencity.dto.achievement.AchievementTranslationVO;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.language.LanguageVO;
import greencity.entity.Achievement;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AchievementVOMapper extends AbstractConverter<Achievement, AchievementVO> {
    @Override
    protected AchievementVO convert(Achievement achievement) {
        List<AchievementTranslationVO> list = new ArrayList<>();
        achievement.getTranslations().forEach(achievementTranslation -> list.add(AchievementTranslationVO.builder()
            .id(achievementTranslation.getId())
            .title(achievementTranslation.getTitle())
            .description(achievementTranslation.getDescription())
            .message(achievementTranslation.getMessage())
            .language(LanguageVO.builder()
                .id(achievementTranslation.getLanguage().getId())
                .code(achievementTranslation.getLanguage().getCode())
                .build())
            .build()));
        return AchievementVO.builder()
            .id(achievement.getId())
            .translations(list)
            .achievementCategory(AchievementCategoryVO.builder()
                .id(achievement.getAchievementCategory().getId())
                .name(achievement.getAchievementCategory().getName())
                .build())
            .condition(achievement.getCondition())
            .build();
    }
}
