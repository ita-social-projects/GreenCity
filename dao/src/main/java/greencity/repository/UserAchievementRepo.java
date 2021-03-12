package greencity.repository;

import greencity.entity.User;
import greencity.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAchievementRepo extends JpaRepository<UserAchievement, Long> {
    /**
     * Method finding achievements with status active.
     *
     * @param userId of {@link User}
     * @return list {@link UserAchievement}
     */
    @Query(value = "SELECT ua FROM UserAchievement ua WHERE ua.user.id =: userId "
        + "AND ua.achievementStatus = 'ACTIVE'"
        + "AND ua.notified = FALSE")
    List<UserAchievement> findAchievementsWithStatusActive(Long userId);
}
