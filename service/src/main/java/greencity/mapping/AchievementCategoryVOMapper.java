package greencity.mapping;

import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.entity.AchievementCategory;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AchievementCategoryVOMapper extends AbstractConverter<AchievementCategory, AchievementCategoryVO> {
    @Override
    protected AchievementCategoryVO convert(AchievementCategory achievementCategory) {
        return AchievementCategoryVO.builder()
            .id(achievementCategory.getId())
            .name(achievementCategory.getName())
            .achievementList(achievementCategory.getAchievementList().stream()
                .map(achievement -> AchievementVO.builder()
                    .id(achievement.getId())
                    .condition(achievement.getCondition())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
}
