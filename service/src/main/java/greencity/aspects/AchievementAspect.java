package greencity.aspects;

import greencity.annotations.AchievementCalculation;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.User;
import greencity.entity.UserAchievement;
import greencity.service.AchievementCategoryService;
import greencity.service.AchievementService;
import greencity.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static greencity.enums.AchievementStatus.ACTIVE;

@Aspect
@Component
@AllArgsConstructor
@Builder
public class AchievementAspect {
    private UserService userService;
    private AchievementService achievementService;
    private AchievementCategoryService achievementCategoryService;
    private final ModelMapper modelMapper;

    /**
     * This pointcut {@link Pointcut} is used for define annotation to processing.
     *
     * @param achievementCalculation is used for recognize methods to processing.
     */
    @Pointcut("@annotation(achievementCalculation)")
    public void myAnnotationPointcut(AchievementCalculation achievementCalculation) {
        /*
         * Complete if needed.
         */
    }

    /**
     * This method is used for User {@link User} achievement calculation.
     *
     * @param achievementCalculation is used for recognize methods to processing.
     * @author Orest Mamchuk
     */
    @AfterReturning(pointcut = "myAnnotationPointcut(achievementCalculation)",
        argNames = "achievementCalculation")
    private void achievementCalculation(AchievementCalculation achievementCalculation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = modelMapper.map(userService.findByEmail(authentication.getName()), User.class);
        UserActionVO userActionByUserId = achievementService.findUserActionByUserId(user.getId());
        AchievementCategoryVO byName = achievementCategoryService.findByName(achievementCalculation.category());
        int condition = updateUserAction(achievementCalculation.category(), userActionByUserId);
        AchievementVO achievementVO = achievementService.findByCategoryIdAndCondition(byName.getId(), condition);
        if (achievementVO != null) {
            changeAchievementStatus(user, achievementVO);
        }
    }

    @AchievementCalculation(category = "Achievements")
    private void changeAchievementStatus(User user, AchievementVO achievementVO){
        Optional<UserAchievement> userAchievement = user.getUserAchievements().stream()
                .filter(userAchievement1 -> userAchievement1.getAchievement().getId().equals(achievementVO.getId()))
                .findFirst();
        if (userAchievement.isPresent()) {
            userAchievement.get().setAchievementStatus(ACTIVE);
            userService.save(modelMapper.map(user, UserVO.class));
        }
    }

    private int updateUserAction(String achievementCategory, UserActionVO userActionVO) {
        Integer action = 0;
        switch (achievementCategory) {
            case "EcoNews":
                action = userActionVO.getEcoNews();
                userActionVO.setEcoNews(++action);
                break;
            case "EcoNewsLikes":
                action = userActionVO.getEcoNewsLikes();
                userActionVO.setEcoNewsLikes(++action);
                break;
            case "EcoNewsComments":
                action = userActionVO.getEcoNewsComments();
                userActionVO.setEcoNewsComments(++action);
                break;
            case "Tips&TricksLikes":
                action = userActionVO.getTipsAndTricksLikes();
                userActionVO.setTipsAndTricksLikes(++action);
                break;
            case "Tips&TricksComments":
                action = userActionVO.getTipsAndTricksComments();
                userActionVO.setTipsAndTricksComments(++action);
                break;
            case "AcquiredHabit":
                action = userActionVO.getAcquiredHabit();
                userActionVO.setAcquiredHabit(++action);
                break;
            case "HabitStreak":
                action = userActionVO.getHabitStreak();
                break;
            case "SocialNetworks":
                action = userActionVO.getSocialNetworks();
                break;
            case "Achievements":
                action = userActionVO.getAchievements();
                userActionVO.setAchievements(++action);
                break;
            default:
                return action;
        }
        achievementService.updateUserActions(userActionVO);
        return action;
    }
}
