package greencity.achievement;

import greencity.client.RestClient;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.achievement.UserVOAchievement;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.Achievement;
import greencity.entity.AchievementCategory;
import greencity.entity.User;
import greencity.entity.UserAchievement;
import greencity.enums.AchievementCategoryType;
import greencity.repository.AchievementCategoryRepo;
import greencity.repository.AchievementRepo;
import greencity.repository.UserAchievementRepo;
import greencity.repository.UserRepo;
import greencity.service.AchievementCategoryService;
import greencity.service.AchievementService;
import greencity.service.UserActionService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

import static greencity.enums.AchievementStatus.ACTIVE;

@Component
public class AchievementCalculation {
    private RestClient restClient;
    private UserActionService userActionService;
    private AchievementService achievementService;
    private AchievementCategoryService achievementCategoryService;
    private final ModelMapper modelMapper;
    private UserAchievementRepo userAchievementRepo;
    private final UserRepo userRepo;
    private final AchievementRepo achievementRepo;
    private final AchievementCategoryRepo achievementCategoryRepo;

    /**
     * Constructor for {@link AchievementCalculation}.
     * 
     * @param restClient                 {@link RestClient}
     * @param userActionService          {@link UserActionService}
     * @param achievementService         {@link AchievementService}
     * @param achievementCategoryService {@link AchievementCategoryService}
     * @param modelMapper                {@link ModelMapper}
     */
    public AchievementCalculation(RestClient restClient,
        UserActionService userActionService,
        @Lazy AchievementService achievementService,
        AchievementCategoryService achievementCategoryService,
        ModelMapper modelMapper,
        UserAchievementRepo userAchievementRepo,
        UserRepo userRepo,
        AchievementRepo achievementRepo,
        AchievementCategoryRepo achievementCategoryRepo) {
        this.restClient = restClient;
        this.userActionService = userActionService;
        this.achievementService = achievementService;
        this.achievementCategoryService = achievementCategoryService;
        this.modelMapper = modelMapper;
        this.userAchievementRepo = userAchievementRepo;
        this.userRepo = userRepo;
        this.achievementRepo = achievementRepo;
        this.achievementCategoryRepo = achievementCategoryRepo;
    }

    /**
     * Method that changing user actions. {@link greencity.entity.UserAction}
     *
     * @param userId   of {@link User}
     * @param category {@link AchievementCategoryType}
     * @param count    number of specific actions
     * @author Orest Mamchuk
     */
    public void calculateAchievement(Long userId,
        AchievementCategoryType category, Integer count) {
        AchievementCategoryVO achievementCategoryVO = achievementCategoryService.findByName(category.name());
        UserActionVO userActionVO = userActionService.findUserActionByUserIdAndAchievementCategory(
            userId, achievementCategoryVO.getId());
        count = checkCount(userActionVO);
        userActionService.updateUserActions(userActionVO);
        checkAchievements(achievementCategoryVO.getId(), count, userId);
    }

    /**
     * Method for finding achievements.
     *
     * @param achievementCategoryId of {@link AchievementCategoryType}
     * @param count                 number of specific actions
     * @param userId                of {@link User}
     * @author Orest Mamchuk
     */
    private void checkAchievements(Long achievementCategoryId, Integer count, Long userId) {
        AchievementVO achievementVO = achievementService.findByCategoryIdAndCondition(achievementCategoryId, count);
        if (achievementVO != null) {
            changeAchievementStatus(userId, achievementCategoryId, count);
        }
    }

    /**
     * Method that changing achievement status.
     *
     * @param userId {@link User}
     * @author Orest Mamchuk
     */
    private void changeAchievementStatus(Long userId, Long achievementCategoryId, int count) {
        Achievement achievement =
            achievementRepo.findByAchievementCategoryIdAndCondition(achievementCategoryId, count).get();
        UserAchievement userAchievement = UserAchievement.builder()
            .achievement(achievement)
            .user(userRepo.findById(userId).get())
            .achievementStatus(ACTIVE)
            .build();
        userAchievementRepo.save(userAchievement);
        AchievementCategory achievementCategory =
            achievementCategoryRepo.findByName(AchievementCategoryType.ACHIEVEMENT.toString());
        UserActionVO userActionVO = userActionService.findUserActionByUserIdAndAchievementCategory(
            userId, achievementCategory.getId());
        int countAchievement = checkCount(userActionVO);
        calculateAchievement(userId, AchievementCategoryType.ACHIEVEMENT, countAchievement);

    }

    /**
     * Method check achievement type.
     *
     * @return count action
     */
    private int checkCount(UserActionVO userActionVO) {

        int count = userActionVO.getCount() + 1;
        userActionVO.setCount(count);

        return count;
    }
}
