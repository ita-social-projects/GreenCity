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
        String achievementCategory = achievementCalculation.category().getAchievementCategory();
        AchievementCategoryVO byName = achievementCategoryService.findByName(achievementCategory);
        int condition = updateUserAction(achievementCategory, userActionByUserId);
        AchievementVO byCategoryId = achievementService.findByCategoryIdAndCondition(byName.getId(), condition);
        if(byCategoryId != null){
            Optional<UserAchievement> first = user.getUserAchievements().stream()
                    .filter(userAchievement -> userAchievement.getAchievement().getId().equals(byCategoryId.getId()))
                    .findFirst();
            if (first.isPresent()) {
                first.get().setAchievementStatus(ACTIVE);
                userService.save(modelMapper.map(user, UserVO.class));
            }
        }
    }

    private int updateUserAction(String achievementCategory, UserActionVO userActionVO){
        Integer action = 0;
        if(achievementCategory.equals("EcoNews")){
            action = userActionVO.getEcoNews();
            userActionVO.setEcoNews(++action);
        }
        if(achievementCategory.equals("EcoNewsLikes")){
            action = userActionVO.getEcoNewsLikes();
            userActionVO.setEcoNewsLikes(++action);
        }
        if(achievementCategory.equals("EcoNewsComments")){
            action = userActionVO.getEcoNewsComments();
            userActionVO.setEcoNewsComments(++action);
        }
        if(achievementCategory.equals("Tips&TricksLikes")){
            action = userActionVO.getTipsAndTricksLikes();
            userActionVO.setTipsAndTricksLikes(++action);
        }
        if(achievementCategory.equals("Tips&TricksComments")){
            action = userActionVO.getTipsAndTricksComments();
            userActionVO.setTipsAndTricksComments(++action);
        }
        if(achievementCategory.equals("AcquiredHabit")){
            action = userActionVO.getAcquiredHabit();
            userActionVO.setAcquiredHabit(++action);
        }
        if(achievementCategory.equals("HabitStreak")){
            action = userActionVO.getHabitStreak();
            userActionVO.setHabitStreak(++action);
        }
        if(achievementCategory.equals("SocialNetworks")){
            action = userActionVO.getSocialNetworks();
            userActionVO.setSocialNetworks(++action);
        }
//        if(achievementCategory.equals("Rating")){
//            action = userActionVO.getRating().intValue();
//            userActionVO.setRating(action++);
//        }
        if(achievementCategory.equals("Achievements")){
            action = userActionVO.getAchievements();
            userActionVO.setAchievements(++action);
        }
        achievementService.updateUserActions(userActionVO);
        return action;
    }
}
