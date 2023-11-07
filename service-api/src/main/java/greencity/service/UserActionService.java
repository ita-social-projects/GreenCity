package greencity.service;

import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionVO;
import greencity.dto.habit.HabitVO;

public interface UserActionService {
    /**
     * Method updates {@link UserActionVO}.
     *
     * @param userActionVO {@link UserActionVO}
     * @return {@link UserActionVO}
     * @author Orest Mamchuk
     */
    UserActionVO updateUserActions(UserActionVO userActionVO);

    /**
     * Method find {@link UserActionVO} by id.
     *
     * @param userId     of {@link UserVO}
     * @param categoryId of {@link AchievementCategoryVO}
     * @return {@link UserActionVO}
     * @author Orest Mamchuk
     */
    UserActionVO findUserActionByUserIdAndAchievementCategory(Long userId, Long categoryId);

    /**
     * Method saves {@link UserActionVO}.
     *
     * @param userActionVO {@link UserActionVO}
     * @return {@link UserActionVO}
     * @author Orest Mamchuk
     */
    UserActionVO save(UserActionVO userActionVO);

    /**
     * Method finds {@link UserActionVO} by userId, achievementCategoryId, and
     * habitId.
     *
     * @param userId     the ID of {@link UserVO}
     * @param categoryId the ID of {@link AchievementCategoryVO}
     * @param habitId    the ID of {@link HabitVO}
     * @return UserAction {@link UserActionVO}
     * @author Oksana Spodaryk
     */
    UserActionVO findUserAction(Long userId, Long categoryId, Long habitId);
}
