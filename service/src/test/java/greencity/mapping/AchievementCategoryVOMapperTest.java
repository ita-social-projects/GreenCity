package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.AchievementCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AchievementCategoryVOMapperTest {
    @InjectMocks
    AchievementCategoryVOMapper achievementCategoryVOMapper;

    @Test
    void convert() {
        AchievementCategory achievementCategory = new AchievementCategory(1L, "Name",
            Collections.singletonList(ModelUtils.getAchievement()),
            Collections.singletonList(ModelUtils.getUserAction()));
        AchievementCategoryVO achievementCategoryVO = AchievementCategoryVO.builder()
            .id(achievementCategory.getId())
            .name(achievementCategory.getName())
            .achievementList(achievementCategory.getAchievementList().stream()
                .map(achievement -> AchievementVO.builder()
                    .id(achievement.getId())
                    .condition(achievement.getCondition())
                    .build())
                .collect(Collectors.toList()))
            .userActions(achievementCategory.getUserActions().stream().map(userAction -> UserActionVO.builder()
                .id(userAction.getId())
                .build())
                .collect(Collectors.toList()))
            .build();
        assertEquals(achievementCategoryVO, achievementCategoryVOMapper.convert(achievementCategory));
    }
}
