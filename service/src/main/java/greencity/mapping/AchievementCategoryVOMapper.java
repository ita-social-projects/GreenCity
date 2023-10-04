package greencity.mapping;

import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.entity.AchievementCategory;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class AchievementCategoryVOMapper extends AbstractConverter<AchievementCategory, AchievementCategoryVO> {
    @Override
    protected AchievementCategoryVO convert(AchievementCategory achievementCategory) {
        return AchievementCategoryVO.builder()
            .id(achievementCategory.getId())
            .name(achievementCategory.getName())
            .build();
    }
}
