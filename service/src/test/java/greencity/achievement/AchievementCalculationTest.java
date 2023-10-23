package greencity.achievement;

import greencity.ModelUtils;
import greencity.client.RestClient;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.User;
import greencity.entity.UserAchievement;
import greencity.enums.AchievementCategoryType;
import greencity.enums.AchievementAction;
import greencity.enums.RatingCalculationEnum;
import greencity.exception.exceptions.NotFoundException;
import greencity.rating.RatingCalculation;
import greencity.repository.AchievementCategoryRepo;
import greencity.repository.AchievementRepo;
import greencity.repository.UserAchievementRepo;
import greencity.repository.UserRepo;
import greencity.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import greencity.entity.Achievement;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    @Mock
    private RatingCalculationEnum ratingCalculationEnum;
    @InjectMocks
    private AchievementCalculation achievementCalculation;
    @Mock
    private UserService userService;
    @Mock
    private RatingCalculation ratingCalculation;

    @Test
    void calculateAchievement_reasonNull() {
        AchievementCategoryVO achievementCategoryVO = new AchievementCategoryVO(1L, "ACHIEVEMENT");
        AchievementCategoryVO achievementCategoryVO2 = new AchievementCategoryVO(2L, "CREATE_NEWS");
        UserVO userVO = ModelUtils.getUserVO();
        UserActionVO userActionVO = new UserActionVO(1L, userVO, achievementCategoryVO, 0);
        User user = ModelUtils.getUser();
        Achievement achievement = ModelUtils.getAchievement();
        achievement.setTitle("bla bla");
        UserAchievement userAchievement = ModelUtils.getUserAchievement();
        user.setUserAchievements(Collections.singletonList(userAchievement));
        AchievementVO achievementVO =
            new AchievementVO(1L, "CREATED_5_NEWS", "CREATED_5_NEWS", "CREATED_5_NEWS", new AchievementCategoryVO(), 1);
        when(achievementCategoryService.findByName(AchievementCategoryType.CREATE_NEWS.name()))
            .thenReturn(achievementCategoryVO);
        when(userActionService.findUserActionByUserIdAndAchievementCategory(any(), any())).thenReturn(userActionVO);
        when(achievementRepo.findByAchievementCategoryIdAndCondition(anyLong(), any()))
            .thenReturn(Optional.of(achievement));
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(achievementCategoryService.findByName("CREATE_NEWS")).thenReturn(achievementCategoryVO2);
        when(achievementService.findByCategoryIdAndCondition(2L, 1)).thenReturn(achievementVO);

//        assertThrows(NotFoundException.class, () -> achievementCalculation.calculateAchievement(1L,
//            AchievementCategoryType.CREATE_NEWS, AchievementAction.ASSIGN));
    }

    @Test
    void calculateAchievement_UNDO_reasonNull() {
        AchievementCategoryVO achievementCategoryVO = new AchievementCategoryVO(1L, "ACHIEVEMENT");
        AchievementCategoryVO achievementCategoryVO2 = new AchievementCategoryVO(2L, "CREATE_NEWS");
        UserVO userVO = ModelUtils.getUserVO();
        UserActionVO userActionVO = new UserActionVO(1L, userVO, achievementCategoryVO, 0);
        User user = ModelUtils.getUser();
        Achievement achievement = ModelUtils.getAchievement();
        achievement.setTitle("Bla bla");
        UserAchievement userAchievement = ModelUtils.getUserAchievement();
        user.setUserAchievements(Collections.singletonList(userAchievement));
        AchievementVO achievementVO =
            new AchievementVO(1L, "CREATED_5_NEWS___", "CREATED_5_NEWS", "CREATED_5_NEWS", new AchievementCategoryVO(),
                1);
        when(achievementCategoryService.findByName(AchievementCategoryType.CREATE_NEWS.name()))
            .thenReturn(achievementCategoryVO);
        when(userActionService.findUserActionByUserIdAndAchievementCategory(any(), any())).thenReturn(userActionVO);
        when(achievementRepo.findByAchievementCategoryIdAndCondition(anyLong(), any()))
            .thenReturn(Optional.of(achievement));
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(achievementCategoryService.findByName("CREATE_NEWS")).thenReturn(achievementCategoryVO2);
        when(achievementService.findByCategoryIdAndCondition(2L, -1)).thenReturn(achievementVO);

//        assertThrows(NotFoundException.class, () -> achievementCalculation.calculateAchievement(1L,
//            AchievementCategoryType.CREATE_NEWS, AchievementAction.DELETE));
    }

    @Test
    void calculateAchievement() {
        AchievementCategoryVO achievementCategoryVO = new AchievementCategoryVO(1L, "ACHIEVEMENT");
        AchievementCategoryVO achievementCategoryVO2 = new AchievementCategoryVO(2L, "CREATE_NEWS");
        UserVO userVO = ModelUtils.getUserVO();
        UserActionVO userActionVO = new UserActionVO(1L, userVO, achievementCategoryVO, 0);
        User user = ModelUtils.getUser();
        Achievement achievement = ModelUtils.getAchievement();
        UserAchievement userAchievement = ModelUtils.getUserAchievement();
        user.setUserAchievements(Collections.singletonList(userAchievement));
        AchievementVO achievementVO =
            new AchievementVO(1L, "CREATED_5_NEWS", "CREATED_5_NEWS", "CREATED_5_NEWS", new AchievementCategoryVO(), 1);
        when(achievementCategoryService.findByName(AchievementCategoryType.CREATE_NEWS.name()))
            .thenReturn(achievementCategoryVO);
        when(userActionService.findUserActionByUserIdAndAchievementCategory(any(), any())).thenReturn(userActionVO);
        when(achievementRepo.findByAchievementCategoryIdAndCondition(anyLong(), any()))
            .thenReturn(Optional.of(achievement));
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(achievementCategoryService.findByName("ACHIEVEMENT")).thenReturn(achievementCategoryVO);
        when(achievementCategoryService.findByName("CREATE_NEWS")).thenReturn(achievementCategoryVO2);
        when(achievementService.findByCategoryIdAndCondition(2L, 1)).thenReturn(achievementVO);
//        achievementCalculation.calculateAchievement(1L, AchievementCategoryType.CREATE_NEWS, AchievementAction.ASSIGN);
        assertEquals(2, userActionVO.getCount());
    }

    @Test
    void calculateAchievement_UNDO() {
        AchievementCategoryVO achievementCategoryVO = new AchievementCategoryVO(1L, "ACHIEVEMENT");
        AchievementCategoryVO achievementCategoryVO2 = new AchievementCategoryVO(2L, "CREATE_NEWS");
        UserVO userVO = ModelUtils.getUserVO();
        UserActionVO userActionVO = new UserActionVO(1L, userVO, achievementCategoryVO, 0);
        User user = ModelUtils.getUser();
        Achievement achievement = ModelUtils.getAchievement();
        UserAchievement userAchievement = ModelUtils.getUserAchievement();
        user.setUserAchievements(Collections.singletonList(userAchievement));
        AchievementVO achievementVO =
            new AchievementVO(1L, "CREATED_5_NEWS", "CREATED_5_NEWS", "CREATED_5_NEWS", new AchievementCategoryVO(), 1);
        when(achievementCategoryService.findByName(AchievementCategoryType.CREATE_NEWS.name()))
            .thenReturn(achievementCategoryVO);
        when(userActionService.findUserActionByUserIdAndAchievementCategory(any(), any())).thenReturn(userActionVO);
        when(achievementRepo.findByAchievementCategoryIdAndCondition(anyLong(), any()))
            .thenReturn(Optional.of(achievement));
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(achievementCategoryService.findByName("ACHIEVEMENT")).thenReturn(achievementCategoryVO);
        when(achievementCategoryService.findByName("CREATE_NEWS")).thenReturn(achievementCategoryVO2);
        when(achievementService.findByCategoryIdAndCondition(2L, -1)).thenReturn(achievementVO);
//        achievementCalculation.calculateAchievement(1L, AchievementCategoryType.CREATE_NEWS, AchievementAction.DELETE);
        assertEquals(-2, userActionVO.getCount());
    }

    @Test
    void calculateAchievement_achievemmentNull() {
        AchievementCategoryVO achievementCategoryVO = ModelUtils.getAchievementCategoryVO();
        AchievementCategoryVO achievementCategoryVO2 = ModelUtils.getAchievementCategoryVO();
        achievementCategoryVO2.setId(2L);
        UserActionVO userActionVO = ModelUtils.getUserActionVO();
        int count = userActionVO.getCount();
        UserActionVO userActionVO2 = ModelUtils.getUserActionVO();
        userActionVO2.setId(2L);
        User user = ModelUtils.getUser();
        UserAchievement userAchievement = ModelUtils.getUserAchievement();
        user.setUserAchievements(Collections.singletonList(userAchievement));
        when(achievementCategoryService.findByName(AchievementCategoryType.CREATE_NEWS.name()))
            .thenReturn(achievementCategoryVO);
        when(userActionService.findUserActionByUserIdAndAchievementCategory(1L, 1L)).thenReturn(userActionVO);
//        achievementCalculation.calculateAchievement(1L, AchievementCategoryType.CREATE_NEWS, AchievementAction.ASSIGN);
        assertEquals(count + 1, userActionVO.getCount());
    }
}
