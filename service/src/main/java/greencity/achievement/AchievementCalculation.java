package greencity.achievement;

import greencity.constant.ErrorMessage;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.Achievement;
import greencity.entity.User;
import greencity.entity.UserAchievement;
import greencity.enums.AchievementCategoryType;
import greencity.enums.AchievementAction;
import greencity.enums.RatingCalculationEnum;
import greencity.rating.RatingCalculation;
import greencity.repository.AchievementRepo;
import greencity.repository.HabitRepo;
import greencity.repository.UserAchievementRepo;
import greencity.service.AchievementCategoryService;
import greencity.service.AchievementService;
import greencity.service.UserActionService;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class AchievementCalculation {
    private UserActionService userActionService;
    private AchievementService achievementService;
    private AchievementCategoryService achievementCategoryService;
    private UserAchievementRepo userAchievementRepo;
    private final AchievementRepo achievementRepo;
    private final RatingCalculation ratingCalculation;
    private final ModelMapper modelMapper;
    private final HabitRepo habitRepo;

    /**
     * Constructor for initializing the required services and repositories.
     */
    public AchievementCalculation(
        UserActionService userActionService,
        @Lazy AchievementService achievementService,
        AchievementCategoryService achievementCategoryService,
        UserAchievementRepo userAchievementRepo,
        AchievementRepo achievementRepo, RatingCalculation ratingCalculation, ModelMapper modelMapper,
        HabitRepo habitRepo) {
        this.userActionService = userActionService;
        this.achievementService = achievementService;
        this.achievementCategoryService = achievementCategoryService;
        this.userAchievementRepo = userAchievementRepo;
        this.achievementRepo = achievementRepo;
        this.ratingCalculation = ratingCalculation;
        this.modelMapper = modelMapper;
        this.habitRepo = habitRepo;
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
        int count = updateUserActionCount(user, achievementCategoryVO.getId(), achievementAction);
        if (AchievementAction.ASSIGN == achievementAction) {
            saveAchievementToUser(user, achievementCategoryVO.getId(), count, null);
        } else if (AchievementAction.DELETE == achievementAction) {
            deleteAchievementFromUser(user, achievementCategoryVO.getId(), null);
        }
    }

    /**
     * Calculates the achievement based on the user's action.
     *
     * @param user              The user for whom the achievement needs to be
     *                          calculated.
     * @param category          The category of the achievement.
     * @param achievementAction The type of action (e.g., ASSIGN, DELETE).
     * @param habitId           The ID of the habit related to the achievement.
     */
    @Transactional
    public void calculateAchievement(UserVO user, AchievementCategoryType category,
        AchievementAction achievementAction, Long habitId) {
        AchievementCategoryVO achievementCategoryVO = achievementCategoryService.findByName(category.name());
        int count = updateUserActionCount(user, achievementCategoryVO.getId(), achievementAction);
        if (AchievementAction.ASSIGN == achievementAction) {
            saveAchievementToUser(user, achievementCategoryVO.getId(), count, habitId);
        } else if (AchievementAction.DELETE == achievementAction) {
            deleteAchievementFromUser(user, achievementCategoryVO.getId(), habitId);
        }
    }

    private void saveAchievementToUser(UserVO userVO, Long achievementCategoryId, int count, Long habitId) {
        AchievementVO achievementVO = achievementService.findByCategoryIdAndCondition(achievementCategoryId, count);
        if (achievementVO != null) {
            Achievement achievement =
                achievementRepo.findByAchievementCategoryIdAndCondition(achievementCategoryId, count)
                    .orElseThrow(() -> new NoSuchElementException(
                        ErrorMessage.ACHIEVEMENT_CATEGORY_NOT_FOUND_BY_ID + achievementCategoryId));
            UserAchievement userAchievement = UserAchievement.builder()
                .achievement(achievement)
                .user(modelMapper.map(userVO, User.class))
                .build();
            if (habitId != null) {
                userAchievement.setHabit(habitRepo.findById(habitId).orElseThrow(() -> new NoSuchElementException(
                    ErrorMessage.HABIT_NOT_FOUND_BY_ID + habitId)));
            }
            RatingCalculationEnum reason = RatingCalculationEnum.findByName(achievement.getTitle());
            ratingCalculation.ratingCalculation(reason, userVO);
            userAchievementRepo.save(userAchievement);
            calculateAchievement(userVO, AchievementCategoryType.ACHIEVEMENT, AchievementAction.ASSIGN);
        }
    }

    private void deleteAchievementFromUser(UserVO user, Long achievementCategoryId, Long habitId) {
        List<Achievement> achievements;
        if (habitId != null) {
            achievements =
                achievementRepo.findUnAchieved(user.getId(), achievementCategoryId, habitId);
        } else {
            achievements = achievementRepo.findUnAchieved(user.getId(), achievementCategoryId);
        }
        if (!achievements.isEmpty()) {
            achievements.forEach(achievement -> {
                RatingCalculationEnum reason = RatingCalculationEnum.findByName("UNDO_" + achievement.getTitle());
                ratingCalculation.ratingCalculation(reason, user);
                userAchievementRepo.deleteByUserAndAchievementId(user.getId(), achievement.getId());
            });
            calculateAchievement(user, AchievementCategoryType.ACHIEVEMENT, AchievementAction.DELETE);
        }
    }

    private int updateUserActionCount(UserVO user, Long achievementCategoryVOId,
        AchievementAction achievementAction) {
        UserActionVO userActionVO =
            userActionService.findUserActionByUserIdAndAchievementCategory(user.getId(), achievementCategoryVOId);
        int count = userActionVO.getCount() + ((AchievementAction.ASSIGN == achievementAction) ? 1 : -1);
        userActionVO.setCount(count > 0 ? count : 0);
        userActionService.updateUserActions(userActionVO);
        return count;
    }
}
