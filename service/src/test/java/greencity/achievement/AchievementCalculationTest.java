package greencity.achievement;

import greencity.ModelUtils;
import greencity.client.RestClient;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserVOAchievement;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.User;
import greencity.entity.UserAchievement;
import greencity.enums.AchievementCategory;
import greencity.enums.AchievementType;
import greencity.repository.UserAchievementRepo;
import greencity.service.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AchievementCalculationTest {
    @Mock
    private RestClient restClient;
    @Mock
    private UserActionService userActionService;
    @Mock
    private AchievementService achievementService;
    @Mock
    private AchievementCategoryService achievementCategoryService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserAchievementRepo userAchievementRepo;
    @InjectMocks
    private AchievementCalculation achievementCalculation;

    @ParameterizedTest(name = "{index} => type=''{0}''")
    @EnumSource(AchievementType.class)
    void calculateAchievement(AchievementType type) {
        AchievementCategoryVO achievementCategoryVO = ModelUtils.getAchievementCategoryVO();
        AchievementCategoryVO achievementCategoryVO2 = ModelUtils.getAchievementCategoryVO();
        achievementCategoryVO2.setId(2L);
        UserActionVO userActionVO = ModelUtils.getUserActionVO();
        UserActionVO userActionVO2 = ModelUtils.getUserActionVO();
        userActionVO2.setId(2L);
        UserVOAchievement userVOAchievement = ModelUtils.getUserVOAchievement();
        User user = ModelUtils.getUser();
        AchievementVO achievementVO = ModelUtils.getAchievementVO();
        UserAchievement userAchievement = ModelUtils.getUserAchievement();
        user.setUserAchievements(Collections.singletonList(userAchievement));
        when(achievementCategoryService.findByName(AchievementCategory.ECO_NEWS.getCategory()))
            .thenReturn(achievementCategoryVO);
        when(userActionService.findUserActionByUserIdAndAchievementCategory(1L, 1L)).thenReturn(userActionVO);
        when(userActionService.updateUserActions(userActionVO)).thenReturn(userActionVO);
        when(restClient.findUserForAchievement(1L)).thenReturn(userVOAchievement);
        when(achievementService.findByCategoryIdAndCondition(1L, 1)).thenReturn(achievementVO);
        when(modelMapper.map(userVOAchievement, User.class)).thenReturn(user);
        when(userAchievementRepo.save(userAchievement)).thenReturn(userAchievement);
        when(achievementCategoryService.findByName("Achievements")).thenReturn(achievementCategoryVO2);
        when(userActionService.findUserActionByUserIdAndAchievementCategory(1L, 2L)).thenReturn(userActionVO2);
        achievementCalculation.calculateAchievement(1L, type, AchievementCategory.ECO_NEWS, 1);
        verify(userAchievementRepo).save(userAchievement);
    }
}
