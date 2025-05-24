package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.AchievementCategory;
import greencity.entity.User;
import greencity.entity.UserAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserActionVOMapperTest {

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    UserActionVOMapper userActionVOMapper;

    @Test
    void convertTest() {
        UserAction userAction = ModelUtils.getUserAction();
        User user = userAction.getUser();
        UserVO userVO = ModelUtils.getUserVO();
        AchievementCategory achievementCategory = userAction.getAchievementCategory();
        AchievementCategoryVO achievementCategoryVO = ModelUtils.getAchievementCategoryVO();
        UserActionVO expectedResult = UserActionVO.builder()
                .id(userAction.getId())
                .user(userVO)
                .achievementCategory(achievementCategoryVO)
                .count(userAction.getCount())
                .build();

        when(modelMapper.map(user, UserVO.class))
                .thenReturn(userVO);
        when(modelMapper.map(achievementCategory, AchievementCategoryVO.class))
                .thenReturn(achievementCategoryVO);

        UserActionVO actualResult = userActionVOMapper.convert(userAction);

        assertEquals(expectedResult, actualResult);
    }
}
