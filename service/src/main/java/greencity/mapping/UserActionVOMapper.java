package greencity.mapping;

import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.AchievementCategory;
import greencity.entity.User;
import greencity.entity.UserAction;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class UserActionVOMapper extends AbstractConverter<UserAction, UserActionVO> {
    private final ModelMapper modelMapper;

    @Lazy
    public UserActionVOMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    protected UserActionVO convert(UserAction userAction) {
        User user = userAction.getUser();
        UserVO userVO = modelMapper.map(user, UserVO.class);

        AchievementCategory achievementCategory = userAction.getAchievementCategory();
        AchievementCategoryVO achievementCategoryVO = modelMapper.map(achievementCategory, AchievementCategoryVO.class);

        return UserActionVO.builder()
            .id(userAction.getId())
            .user(userVO)
            .achievementCategory(achievementCategoryVO)
            .count(userAction.getCount())
            .build();
    }
}
