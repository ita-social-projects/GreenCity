package greencity.repository;

import greencity.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

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

    /**
     * Retrieves a list of UserAchievement objects associated with a given user ID.
     *
     * @param userId The unique identifier of the user for whom to fetch the
     *               achievements.
     * @return A list of UserAchievement objects related to the specified user ID.
     */
    List<UserAchievement> getUserAchievementByUserId(Long userId);
    /**
     * Retrieves a list of UserAchievement objects associated with a given user ID.
     *
     * @param userId The unique identifier of the user for whom to fetch the
     *               achievements.
     * @return A list of UserAchievement objects related to the specified user ID.
     */
@Transactional
    @Modifying
    @Query(value = "DELETE from user_achievements" +
            "    where user_id=:userId and achievement_id=:achievementId", nativeQuery = true)
    void deleteByUserAndAchievemntId(Long userId, Long achievementId);

}
