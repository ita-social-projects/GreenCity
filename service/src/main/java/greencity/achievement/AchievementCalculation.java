package greencity.achievement;

import greencity.constant.ErrorMessage;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.Achievement;
import greencity.entity.UserAchievement;
import greencity.enums.AchievementCategoryType;
import greencity.enums.AchievementAction;
import greencity.enums.RatingCalculationEnum;
import greencity.rating.RatingCalculation;
import greencity.repository.AchievementRepo;
import greencity.repository.UserAchievementRepo;
import greencity.repository.UserRepo;
import greencity.service.AchievementCategoryService;
import greencity.service.AchievementService;
import greencity.service.UserActionService;
import greencity.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
public class AchievementCalculation {
    private UserActionService userActionService;
    private AchievementService achievementService;
    private UserService userService;
    private AchievementCategoryService achievementCategoryService;
    private UserAchievementRepo userAchievementRepo;
    private final UserRepo userRepo;
    private final AchievementRepo achievementRepo;
    private final RatingCalculation ratingCalculation;

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
        AchievementRepo achievementRepo, RatingCalculation ratingCalculation, UserService userService) {
        this.userActionService = userActionService;
        this.achievementService = achievementService;
        this.achievementCategoryService = achievementCategoryService;
        this.userAchievementRepo = userAchievementRepo;
        this.userRepo = userRepo;
        this.achievementRepo = achievementRepo;
        this.ratingCalculation = ratingCalculation;
        this.userService = userService;
    }

    /**
     * Calculate and handle achievements for a user based on specified category and
     * status.
     *
     * @param userId            The user ID for whom the achievement is to be
     *                          calculated.
     * @param category          The category type of the achievement.
     * @param achievementAction The status of the achievement (ASSIGN, DELETE,
     *                          etc.).
     */
    public void calculateAchievement(Long userId, AchievementCategoryType category,
        AchievementAction achievementAction) {
        AchievementCategoryVO achievementCategoryVO = achievementCategoryService.findByName(category.name());
        UserActionVO userActionVO =
            userActionService.findUserActionByUserIdAndAchievementCategory(userId, achievementCategoryVO.getId());
        int count = updateCount(userActionVO, achievementAction);
        userActionService.updateUserActions(userActionVO);

        checkAchievements(achievementCategoryVO.getId(), count, userId, achievementAction);
    }

    /**
     * Update the count of user actions based on achievement status.
     *
     * @param userActionVO      The user's action data object.
     * @param achievementAction The status of the achievement.
     * @return Updated count of user actions.
     */
    private int updateCount(UserActionVO userActionVO, AchievementAction achievementAction) {
        int count = achievementAction.equals(AchievementAction.ASSIGN) ? userActionVO.getCount() + 1
            : userActionVO.getCount() - 1;
        userActionVO.setCount(count);
        return count;
    }

    /**
     * Check and handle achievements based on specified conditions.
     *
     * @param achievementCategoryId The ID of the achievement category.
     * @param count                 The count of user actions for the achievement.
     * @param userId                The user ID for whom the achievement is to be
     *                              checked.
     * @param achievementAction     The status of the achievement.
     */
    private void checkAchievements(Long achievementCategoryId, Integer count, Long userId,
        AchievementAction achievementAction) {
        AchievementVO achievementVO = achievementService.findByCategoryIdAndCondition(achievementCategoryId, count);
        if (achievementVO != null) {
            if (achievementAction.equals(AchievementAction.ASSIGN)) {
                saveAchievementToUser(userId, achievementCategoryId, count);
            } else {
                deleteAchievementFromUser(userId, achievementCategoryId, count);
            }
        }
    }

    /**
     * Save an achievement to a user's record.
     *
     * @param userId                The user ID.
     * @param achievementCategoryId The achievement category ID.
     * @param count                 The count of user actions for the achievement.
     */

    private void saveAchievementToUser(Long userId, Long achievementCategoryId, int count) {
        Achievement achievement =
            achievementRepo.findByAchievementCategoryIdAndCondition(achievementCategoryId, count)
                .orElseThrow(() -> new NoSuchElementException(
                    ErrorMessage.ACHIEVEMENT_CATEGORY_NOT_FOUND_BY_ID + achievementCategoryId));
        UserAchievement userAchievement = UserAchievement.builder()
            .achievement(achievement)
            .user(userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId)))
            .build();
        RatingCalculationEnum reason = RatingCalculationEnum.findEnumByName(achievement.getTitle());
        UserVO user = userService.findById(userId);
        ratingCalculation.ratingCalculation(reason, user);
        userAchievementRepo.save(userAchievement);
        calculateAchievement(userId, AchievementCategoryType.ACHIEVEMENT, AchievementAction.ASSIGN);
    }

    /**
     * Delete an achievement from a user's record.
     *
     * @param userId                The user ID.
     * @param achievementCategoryId The achievement category ID.
     * @param count                 The count of user actions for the achievement.
     */
    private void deleteAchievementFromUser(Long userId, Long achievementCategoryId, int count) {
        Achievement achievement =
            achievementRepo.findByAchievementCategoryIdAndCondition(achievementCategoryId, count)
                .orElseThrow(() -> new NoSuchElementException(
                    ErrorMessage.ACHIEVEMENT_CATEGORY_NOT_FOUND_BY_ID + achievementCategoryId));

        UserAchievement userAchievement = UserAchievement.builder()
            .achievement(achievement)
            .user(userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId)))
            .build();

        RatingCalculationEnum reason = RatingCalculationEnum.findEnumByName("UNDO_" + achievement.getTitle());
        UserVO user = userService.findById(userId);
        ratingCalculation.ratingCalculation(reason, user);
        userAchievementRepo.delete(userAchievement);
        calculateAchievement(userId, AchievementCategoryType.ACHIEVEMENT, AchievementAction.DELETE);
    }
}
