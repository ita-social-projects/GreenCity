package greencity.achievement;

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
import greencity.service.AchievementCategoryService;
import greencity.service.AchievementService;
import greencity.service.UserActionService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static greencity.enums.AchievementStatus.ACTIVE;
import static greencity.enums.AchievementType.INCREMENT;

@Component
public class AchievementCalculation {
    private RestClient restClient;
    private UserActionService userActionService;
    private AchievementService achievementService;
    private AchievementCategoryService achievementCategoryService;
    private final ModelMapper modelMapper;
    private UserAchievementRepo userAchievementRepo;

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
        UserAchievementRepo userAchievementRepo) {
        this.restClient = restClient;
        this.userActionService = userActionService;
        this.achievementService = achievementService;
        this.achievementCategoryService = achievementCategoryService;
        this.modelMapper = modelMapper;
        this.userAchievementRepo = userAchievementRepo;
    }

    /**
     * Method that changing user actions. {@link greencity.entity.UserAction}
     *
     * @param userId   of {@link User}
     * @param type     of action
     * @param category {@link AchievementCategory}
     * @param count    number of specific actions
     * @author Orest Mamchuk
     */
    public void calculateAchievement(Long userId, AchievementType type, AchievementCategory category, Integer count) {
        AchievementCategoryVO achievementCategoryVO = achievementCategoryService.findByName(category.getCategory());
        UserActionVO userActionVO = userActionService.findUserActionByUserIdAndAchievementCategory(
            userId, achievementCategoryVO.getId());
        count = checkType(type, userActionVO, count);
        userActionService.updateUserActions(userActionVO);
        checkAchievements(achievementCategoryVO.getId(), count, userId);
    }

    /**
     * Method for finding achievements.
     *
     * @param achievementCategoryId of {@link AchievementCategory}
     * @param count                 number of specific actions
     * @param userId                of {@link User}
     * @author Orest Mamchuk
     */
    private void checkAchievements(Long achievementCategoryId, Integer count, Long userId) {
        UserVOAchievement userForAchievement = restClient.findUserForAchievement(userId);
        AchievementVO achievementVO = achievementService.findByCategoryIdAndCondition(achievementCategoryId, count);
        if (achievementVO != null) {
            changeAchievementStatus(modelMapper.map(userForAchievement, User.class), achievementVO);
        }
    }

    /**
     * Method that changing achievement status.
     *
     * @param user          {@link User}
     * @param achievementVO {@link AchievementVO}
     * @author Orest Mamchuk
     */
    private void changeAchievementStatus(User user, AchievementVO achievementVO) {
        Optional<UserAchievement> userAchievement = user.getUserAchievements().stream()
            .filter(userAchievement1 -> userAchievement1.getAchievement().getId().equals(achievementVO.getId()))
            .findFirst();
        if (userAchievement.isPresent()) {
            UserAchievement achievement = userAchievement.get();
            achievement.setAchievementStatus(ACTIVE);
            userAchievementRepo.save(achievement);
            calculateAchievement(user.getId(), INCREMENT, AchievementCategory.ACHIEVEMENTS, 0);
        }
    }

    /**
     * Method check achievement type.
     *
     * @param type  of action
     * @param count number of specific actions
     * @return count action
     */
    private int checkType(AchievementType type, UserActionVO userActionVO, Integer count) {
        switch (type) {
            case INCREMENT:
                count = userActionVO.getCount() + 1;
                userActionVO.setCount(count);
                break;
            case SETTER:
                userActionVO.setCount(count);
                break;
            case COMPARISON:
                if (userActionVO.getCount() < count) {
                    userActionVO.setCount(count);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        return count;
    }
}
