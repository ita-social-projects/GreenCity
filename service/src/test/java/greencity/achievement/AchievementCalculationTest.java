package greencity.achievement;

import greencity.ModelUtils;
import greencity.client.RestClient;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserVOAchievement;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.AchievementCategory;
import greencity.entity.User;
import greencity.entity.UserAchievement;
import greencity.enums.AchievementCategoryType;
import greencity.repository.AchievementCategoryRepo;
import greencity.repository.AchievementRepo;
import greencity.repository.UserAchievementRepo;
import greencity.repository.UserRepo;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
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
    @Mock
    private AchievementRepo achievementRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private AchievementCategoryRepo achievementCategoryRepo;
    @InjectMocks
    private AchievementCalculation achievementCalculation;

    @Test
    void calculateAchievement() {
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
        when(achievementCategoryService.findByName(AchievementCategoryType.CREATE_NEWS.name()))
            .thenReturn(achievementCategoryVO);
        when(userActionService.findUserActionByUserIdAndAchievementCategory(1L, 1L)).thenReturn(userActionVO);
//when(achievementRepo.findByAchievementCategoryIdAndCondition(anyLong(),anyInt())).thenReturn(Optional.of(ModelUtils.getAchievement()));
//        when(userRepo.findById(anyLong())).thenReturn(Optional.of(ModelUtils.getUser()));
//        when(achievementCategoryRepo.findByName(anyString())).thenReturn(ModelUtils.getAchievementCategory());


        achievementCalculation.calculateAchievement(1L, AchievementCategoryType.CREATE_NEWS);
    }
}
