package greencity.achievement;

import greencity.constant.ErrorMessage;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.Achievement;
import greencity.entity.AchievementCategory;
import greencity.entity.UserAchievement;
import greencity.enums.AchievementCategoryType;
import greencity.enums.AchievementAction;
import greencity.enums.RatingCalculationEnum;
import greencity.rating.RatingCalculation;
import greencity.repository.*;
import greencity.service.AchievementCategoryService;
import greencity.service.AchievementService;
import greencity.service.UserActionService;
import greencity.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
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
    private final UserActionRepo userActionRepo;
    private final AchievementCategoryRepo achievementCategoryRepo;

    /**
     * Constructor for initializing the required services and repositories.
     *
     * @param userActionService  Service for user actions.
     * @param achievementService Service for achievements. ... other parameters ...
     */
    public AchievementCalculation(
        UserActionService userActionService,
        @Lazy AchievementService achievementService,
        AchievementCategoryService achievementCategoryService,
        UserAchievementRepo userAchievementRepo,
        UserRepo userRepo,
        AchievementRepo achievementRepo, RatingCalculation ratingCalculation, UserService userService,
        UserActionRepo userActionRepo,
        AchievementCategoryRepo achievementCategoryRepo) {
        this.userActionService = userActionService;
        this.achievementService = achievementService;
        this.achievementCategoryService = achievementCategoryService;
        this.userAchievementRepo = userAchievementRepo;
        this.userRepo = userRepo;
        this.achievementRepo = achievementRepo;
        this.ratingCalculation = ratingCalculation;
        this.userService = userService;
        this.userActionRepo = userActionRepo;
        this.achievementCategoryRepo = achievementCategoryRepo;
    }

    /**
     * Calculates the achievement based on the user's action.
     *
     * @param user              The user for whom the achievement needs to be
     *                          calculated.
     * @param category          The category of the achievement.
     * @param achievementAction The type of action (e.g., ASSIGN, DELETE).
     */
    @Transactional
    public void calculateAchievement(UserVO user, AchievementCategoryType category,
        AchievementAction achievementAction) {
        AchievementCategoryVO achievementCategoryVO = achievementCategoryService.findByName(category.name());
        UserActionVO userActionVO =
            userActionService.findUserActionByUserIdAndAchievementCategory(user.getId(), achievementCategoryVO.getId());
        int count = updateCount(userActionVO, achievementAction);
        userActionService.updateUserActions(userActionVO);
        if (achievementAction.equals(AchievementAction.ASSIGN)) {
            saveAchievementToUser(user, achievementCategoryVO.getId(), count, achievementAction);
        } else {
            deleteAchievementFromUser(user, achievementCategoryVO.getId());
        }
    }

    private int updateCount(UserActionVO userActionVO, AchievementAction achievementAction) {
        int count = achievementAction.equals(AchievementAction.ASSIGN) ? userActionVO.getCount() + 1
            : userActionVO.getCount() - 1;
        userActionVO.setCount(count);
        return count;
    }

    private void saveAchievementToUser(UserVO user, Long achievementCategoryId, int count,
        AchievementAction achievementAction) {
        AchievementVO achievementVO = achievementService.findByCategoryIdAndCondition(achievementCategoryId, count);
        if (achievementVO != null) {
            if (achievementAction.equals(AchievementAction.ASSIGN)) {
                Achievement achievement =
                    achievementRepo.findByAchievementCategoryIdAndCondition(achievementCategoryId, count)
                        .orElseThrow(() -> new NoSuchElementException(
                            ErrorMessage.ACHIEVEMENT_CATEGORY_NOT_FOUND_BY_ID + achievementCategoryId));
                UserAchievement userAchievement = UserAchievement.builder()
                    .achievement(achievement)
                    .user(userRepo.findById(user.getId())
                        .orElseThrow(
                            () -> new NoSuchElementException(ErrorMessage.USER_NOT_FOUND_BY_ID + user.getId())))
                    .build();
                RatingCalculationEnum reason = RatingCalculationEnum.findByName(achievement.getTitle());
                ratingCalculation.ratingCalculation(reason, user);
                userAchievementRepo.save(userAchievement);
                calculateAchievement(user, AchievementCategoryType.ACHIEVEMENT, AchievementAction.ASSIGN);
            }
        }
    }

    private void deleteAchievementFromUser(UserVO user, Long achievementCategoryId) {
        List<Achievement> achievements =
            achievementRepo.findUnAchieved(user.getId(), achievementCategoryId);
        achievements.forEach(achievement -> {
            RatingCalculationEnum reason = RatingCalculationEnum.findByName("UNDO_" + achievement.getTitle());
            AchievementCategory achievementCategory = achievementCategoryRepo.findByName("ACHIEVEMENT");
            UserActionVO userActionVO =
                userActionService.findUserActionByUserIdAndAchievementCategory(user.getId(),
                    achievementCategory.getId());
            updateCount(userActionVO, AchievementAction.DELETE);
            userActionService.updateUserActions(userActionVO);
            ratingCalculation.ratingCalculation(reason, user);
            Long achievementId = achievement.getId();
            Long userId = user.getId();

            userAchievementRepo.deleteByUserAndAchievemntId(userId, achievementId);
        });
    }
}
