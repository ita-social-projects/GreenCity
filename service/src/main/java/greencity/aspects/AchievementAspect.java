package greencity.aspects;

import greencity.annotations.AchievementCalculation;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.User;
import greencity.entity.UserAchievement;
import greencity.repository.UserActionRepo;
import greencity.service.AchievementCategoryService;
import greencity.service.AchievementService;
import greencity.service.UserActionService;
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
import java.util.concurrent.CompletableFuture;

import static greencity.enums.AchievementStatus.ACTIVE;

@Aspect
@Component
@AllArgsConstructor
@Builder
public class AchievementAspect {
    private UserService userService;
    private UserActionService userActionService;
    private AchievementService achievementService;
    private AchievementCategoryService achievementCategoryService;
    private final ModelMapper modelMapper;
    private final UserActionRepo userActionRepo;

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
        int condition = userActionRepo.findActionCountAccordToCategory(achievementCalculation.column(), user.getId());
        AchievementCategoryVO byName = achievementCategoryService.findByName(achievementCalculation.category());
        AchievementVO achievementVO = achievementService.findByCategoryIdAndCondition(byName.getId(), condition);
        if (achievementVO != null) {
            changeAchievementStatus(user, achievementVO);
        }
    }

    private void changeAchievementStatus(User user, AchievementVO achievementVO) {
        Optional<UserAchievement> userAchievement = user.getUserAchievements().stream()
            .filter(userAchievement1 -> userAchievement1.getAchievement().getId().equals(achievementVO.getId()))
            .findFirst();
        if (userAchievement.isPresent()) {
            userAchievement.get().setAchievementStatus(ACTIVE);
            userService.save(modelMapper.map(user, UserVO.class));
            CompletableFuture.runAsync(() -> calculateAchievements(user));
        }
    }

    @AchievementCalculation(category = "Achievements", column = "achievements")
    public void calculateAchievements(User user) {
        UserActionVO userActionVO = userActionService.findUserActionByUserId(user.getId());
        userActionVO.setAchievements(userActionVO.getAchievements() + 1);
        userActionService.updateUserActions(userActionVO);
    }
}
