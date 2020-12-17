package greencity.service;

import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionVO;

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
}
