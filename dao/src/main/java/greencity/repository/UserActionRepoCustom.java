package greencity.repository;

import greencity.entity.AchievementCategory;
import greencity.entity.User;
import greencity.entity.UserAction;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActionRepoCustom {
    /**
     * {@inheritDoc} Method to find UserAction {@link UserAction}
     *
     * @param category {@link AchievementCategory}
     * @param userId   of {@link User}
     * @return value in column in UserAction {@link UserAction}
     */
    Integer findActionCountAccordToCategory(String category, Long userId);
}
