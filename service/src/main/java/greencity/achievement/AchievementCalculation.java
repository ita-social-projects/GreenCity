package greencity.achievement;

import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.AchievementCategory;
import greencity.entity.User;
import greencity.entity.UserAchievement;
import greencity.enums.AchievementType;
import greencity.service.AchievementCategoryService;
import greencity.service.AchievementService;
import greencity.service.UserActionService;
import greencity.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static greencity.enums.AchievementStatus.ACTIVE;
import static greencity.enums.AchievementType.INCREMENT;

@Component
public class AchievementCalculation {
    private UserService userService;
    private UserActionService userActionService;
    private AchievementService achievementService;
    private AchievementCategoryService achievementCategoryService;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc} Constructor for {@link AchievementCalculation}
     * 
     * @param userService                {@link UserService}
     * @param userActionService          {@link UserActionService}
     * @param achievementService         {@link AchievementService}
     * @param achievementCategoryService {@link AchievementCategoryService}
     * @param modelMapper                {@link ModelMapper}
     */
    public AchievementCalculation(@Lazy UserService userService,
        UserActionService userActionService,
        @Lazy AchievementService achievementService,
        AchievementCategoryService achievementCategoryService,
        ModelMapper modelMapper) {
        this.userService = userService;
        this.userActionService = userActionService;
        this.achievementService = achievementService;
        this.achievementCategoryService = achievementCategoryService;
        this.modelMapper = modelMapper;
    }

    /**
     * {@inheritDoc} Method that changing user actions
     * {@link greencity.entity.UserAction}
     *
     * @param userId   of {@link User}
     * @param type     of action
     * @param category {@link AchievementCategory}
     * @param count    number of specific actions
     * @author Orest Mamchuk
     */
    public void calculateAchievement(Long userId, AchievementType type, String category, Integer count) {
        AchievementCategoryVO achievementCategoryVO = achievementCategoryService.findByName(category);
        UserActionVO userActionVO = userActionService.findUserActionByUserIdAndAchievementCategory(
            userId, achievementCategoryVO.getId());
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
        userActionService.updateUserActions(userActionVO);
        checkAchievements(achievementCategoryVO.getId(), count, userId);
    }

    /**
     * {@inheritDoc} Method for finding achievements
     *
     * @param achievementCategoryId of {@link AchievementCategory}
     * @param count                 number of specific actions
     * @param userId                of {@link User}
     * @author Orest Mamchuk
     */
    private void checkAchievements(Long achievementCategoryId, Integer count, Long userId) {
        UserVO userVO = userService.findById(userId);
        AchievementVO achievementVO = achievementService.findByCategoryIdAndCondition(achievementCategoryId, count);
        if (achievementVO != null) {
            changeAchievementStatus(modelMapper.map(userVO, User.class), achievementVO);
        }
    }

    /**
     * {@inheritDoc} Method that changing achievement status
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
            userAchievement.get().setAchievementStatus(ACTIVE);
            userService.save(modelMapper.map(user, UserVO.class));
            calculateAchievement(user.getId(), INCREMENT, "Achievements", 0);
        }
    }
}
