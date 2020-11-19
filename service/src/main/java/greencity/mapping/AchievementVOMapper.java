package greencity.mapping;

import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.entity.Achievement;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AchievementVOMapper extends AbstractConverter<Achievement, AchievementVO> {
    @Override
    protected AchievementVO convert(Achievement achievement) {
        return AchievementVO.builder()
                .id(achievement.getId())
                .title(achievement.getTitle())
                .description(achievement.getDescription())
                .message(achievement.getMessage())
                .userAchievements(achievement.getUserAchievements().stream().map(userAchievement ->
                        UserAchievementVO.builder()
                                .achievement(AchievementVO.builder()
                                        .id(userAchievement.getId())
                                        .build())
                                .build()).collect(Collectors.toList()))
                .achievementCategory(AchievementCategoryVO.builder()
                        .id(achievement.getAchievementCategory().getId())
                        .name(achievement.getAchievementCategory().getName())
                        .build())
                .condition(achievement.getCondition())
                .build();
    }
}
