package greencity.repository;

import greencity.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAchievementRepo extends JpaRepository<UserAchievement, Long> {
    /**
     * Method finding user achievement by user id and achievement id.
     *
     * @param userId        {@link Long}
     * @param achievementId {@link Long}
     * @return list {@link UserAchievement}
     */
    @Query(value = "FROM UserAchievement u WHERE u.user.id =:userId AND u.achievement.id =:achievementId")
    UserAchievement getUserAchievementByIdAndAchievementId(Long userId, Long achievementId);
}
