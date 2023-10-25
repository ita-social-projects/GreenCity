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
import greencity.repository.UserAchievementRepo;
import greencity.repository.AchievementCategoryRepo;
import greencity.service.AchievementCategoryService;
import greencity.service.AchievementService;
import greencity.service.UserActionService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
    private final AchievementCategoryRepo achievementCategoryRepo;
    private final ModelMapper modelMapper;
    /**
     * The unique identifier for the achievement with the name "ACHIEVEMENT".
     */
    private Long achievementId;

    /**
     * Constructor for initializing the required services and repositories.
     */
    public AchievementCalculation(
        UserActionService userActionService,
        @Lazy AchievementService achievementService,
        AchievementCategoryService achievementCategoryService,
        UserAchievementRepo userAchievementRepo,
        AchievementRepo achievementRepo, RatingCalculation ratingCalculation,
        AchievementCategoryRepo achievementCategoryRepo, ModelMapper modelMapper) {
        this.userActionService = userActionService;
        this.achievementService = achievementService;
        this.achievementCategoryService = achievementCategoryService;
        this.userAchievementRepo = userAchievementRepo;
        this.achievementRepo = achievementRepo;
        this.ratingCalculation = ratingCalculation;
        this.achievementCategoryRepo = achievementCategoryRepo;
        this.modelMapper = modelMapper;
    }

    @PostConstruct
    private void initAchievementId() {
        this.achievementId = achievementCategoryRepo.findByName("ACHIEVEMENT").getId();
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
        if (AchievementAction.ASSIGN.equals(achievementAction)) {
            int count = incrementCount(userActionVO);
            userActionService.updateUserActions(userActionVO);
            saveAchievementToUser(user, achievementCategoryVO.getId(), count);
        } else if (AchievementAction.DELETE.equals(achievementAction)) {
            decrementCount(userActionVO);
            userActionService.updateUserActions(userActionVO);
            deleteAchievementFromUser(user, achievementCategoryVO.getId());
        }
    }

    private int incrementCount(UserActionVO userActionVO) {
        int count = userActionVO.getCount() + 1;
        userActionVO.setCount(count);
        return count;
    }

    private int decrementCount(UserActionVO userActionVO) {
        int count = userActionVO.getCount() - 1;
        userActionVO.setCount(count);
        return count;
    }

    private void saveAchievementToUser(UserVO userVO, Long achievementCategoryId, int count) {
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
            RatingCalculationEnum reason = RatingCalculationEnum.findByName(achievement.getTitle());
            ratingCalculation.ratingCalculation(reason, userVO);
            userAchievementRepo.save(userAchievement);
            calculateAchievement(userVO, AchievementCategoryType.ACHIEVEMENT, AchievementAction.ASSIGN);
        }
    }

    private void deleteAchievementFromUser(UserVO user, Long achievementCategoryId) {
        List<Achievement> achievements =
            achievementRepo.findUnAchieved(user.getId(), achievementCategoryId);
        achievements.forEach(achievement -> {
            RatingCalculationEnum reason = RatingCalculationEnum.findByName("UNDO_" + achievement.getTitle());
            UserActionVO userActionVO =
                userActionService.findUserActionByUserIdAndAchievementCategory(user.getId(), achievementId);
            decrementCount(userActionVO);
            userActionService.updateUserActions(userActionVO);
            ratingCalculation.ratingCalculation(reason, user);
            userAchievementRepo.deleteByUserAndAchievemntId(user.getId(), achievement.getId());
        });
    }
}
