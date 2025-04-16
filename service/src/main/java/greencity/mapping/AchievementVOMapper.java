package greencity.mapping;

import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.entity.Achievement;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class AchievementVOMapper extends AbstractConverter<Achievement, AchievementVO> {
    @Override
    protected AchievementVO convert(Achievement achievement) {
        return AchievementVO.builder()
            .id(achievement.getId())
            .condition(achievement.getCondition())
            .title(achievement.getTitle())
            .name(achievement.getName())
            .nameEng(achievement.getNameEng())
            .condition(achievement.getCondition())
            .achievementCategory(AchievementCategoryVO.builder()
                .id(achievement.getAchievementCategory().getId())
                .name(achievement.getAchievementCategory().getName())
                .build())
            .build();
    }
}
