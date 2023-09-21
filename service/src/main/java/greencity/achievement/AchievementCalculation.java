package greencity.achievement;

import greencity.constant.ErrorMessage;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.Achievement;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
public class AchievementCalculation {
    private UserActionService userActionService;
    private AchievementService achievementService;
    private AchievementCategoryService achievementCategoryService;
    private UserAchievementRepo userAchievementRepo;
    private final UserRepo userRepo;
    private final AchievementRepo achievementRepo;

    /**
     * Constructor for {@link AchievementCalculation}.
     * 
     * @param userActionService          {@link UserActionService}
     * @param achievementService         {@link AchievementService}
     * @param achievementCategoryService {@link AchievementCategoryService}
     */
    public AchievementCalculation(
        UserActionService userActionService,
        @Lazy AchievementService achievementService,
        AchievementCategoryService achievementCategoryService,
        UserAchievementRepo userAchievementRepo,
        UserRepo userRepo,
        AchievementRepo achievementRepo,
        AchievementCategoryRepo achievementCategoryRepo) {
        this.userActionService = userActionService;
        this.achievementService = achievementService;
        this.achievementCategoryService = achievementCategoryService;
        this.userAchievementRepo = userAchievementRepo;
        this.userRepo = userRepo;
        this.achievementRepo = achievementRepo;
    }

    /**
     * Method that changing user actions. {@link greencity.entity.UserAction}
     *
     * @param userId   of {@link User}
     * @param category {@link AchievementCategoryType}
     * @author Orest Mamchuk
     */
    public void calculateAchievement(Long userId,
        AchievementCategoryType category) {
        AchievementCategoryVO achievementCategoryVO = achievementCategoryService.findByName(category.name());
        UserActionVO userActionVO = userActionService.findUserActionByUserIdAndAchievementCategory(
            userId, achievementCategoryVO.getId());
        int count = userActionVO.getCount() + 1;
        userActionVO.setCount(count);
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
            achievementRepo.findByAchievementCategoryIdAndCondition(achievementCategoryId, count)
                .orElseThrow(() -> new NoSuchElementException(
                    ErrorMessage.ACHIEVEMENT_CATEGORY_NOT_FOUND_BY_ID + achievementCategoryId));
        UserAchievement userAchievement = UserAchievement.builder()
            .achievement(achievement)
            .user(userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId)))
            .build();
        userAchievementRepo.save(userAchievement);
        calculateAchievement(userId, AchievementCategoryType.ACHIEVEMENT);
    }
}
