package greencity.repository;

import greencity.entity.AchievementCategory;
import greencity.entity.User;
import greencity.entity.UserAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActionRepo extends JpaRepository<UserAction, Long> {
    /**
     * Method find {@link UserAction} by userId and achievementCategoryId.
     *
     * @param userId                of {@link User}
     * @param achievementCategoryId of {@link AchievementCategory}
     * @return UserAction {@link UserAction}
     * @author Orest Mamchuk
     */
    @Query(value = "SELECT ua FROM UserAction ua "
        + "WHERE ua.achievementCategory.id = :achievementCategoryId "
        + "AND ua.user.id = :userId")
    UserAction findByUserIdAndAchievementCategoryId(Long userId, Long achievementCategoryId);
}
