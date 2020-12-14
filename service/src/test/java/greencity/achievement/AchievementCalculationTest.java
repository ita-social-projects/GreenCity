package greencity.achievement;

import greencity.ModelUtils;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.User;
import greencity.entity.UserAchievement;
import greencity.enums.AchievementType;
import greencity.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AchievementCalculationTest {
    @Mock
    private UserService userService;
    @Mock
    private UserActionService userActionService;
    @Mock
    private AchievementService achievementService;
    @Mock
    private AchievementCategoryService achievementCategoryService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private AchievementCalculation achievementCalculation;

    @ParameterizedTest(name = "{index} => type=''{0}''")
    @EnumSource(AchievementType.class)
    void calculateAchievement(AchievementType type) {
        String category = "EcoNewsLikes";
        AchievementCategoryVO achievementCategoryVO = ModelUtils.getAchievementCategoryVO();
        AchievementCategoryVO achievementCategoryVO2 = ModelUtils.getAchievementCategoryVO();
        achievementCategoryVO2.setId(2L);
        UserActionVO userActionVO = ModelUtils.getUserActionVO();
        UserActionVO userActionVO2 = ModelUtils.getUserActionVO();
        userActionVO2.setId(2L);
        UserVO userVO = ModelUtils.getUserVO();
        User user = ModelUtils.getUser();
        AchievementVO achievementVO = ModelUtils.getAchievementVO();
        UserAchievement userAchievement = ModelUtils.getUserAchievement();
        user.setUserAchievements(Collections.singletonList(userAchievement));
        when(achievementCategoryService.findByName(category)).thenReturn(achievementCategoryVO);
        when(userActionService.findUserActionByUserIdAndAchievementCategory(1L, 1L)).thenReturn(userActionVO);
        when(userActionService.updateUserActions(userActionVO)).thenReturn(userActionVO);
        when(userService.findByIdTransactional(1L)).thenReturn(userVO);
        when(achievementService.findByCategoryIdAndCondition(1L, 1)).thenReturn(achievementVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        when(userService.save(userVO)).thenReturn(userVO);
        when(achievementCategoryService.findByName("Achievements")).thenReturn(achievementCategoryVO2);
        when(userActionService.findUserActionByUserIdAndAchievementCategory(1L, 2L)).thenReturn(userActionVO2);
        achievementCalculation.calculateAchievement(1L, type, category, 1);
        verify(userService).save(userVO);
    }
}
