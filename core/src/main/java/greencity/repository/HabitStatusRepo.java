package greencity.repository;

import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatus;
import greencity.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitStatusRepo extends JpaRepository<HabitStatus, Long> {
    /**
     * Method return {@link HabitStatus} by habitAssignId.
     *
     * @param habitAssignId - id of {@link HabitAssign}
     * @return {@link HabitStatus}
     */
    Optional<HabitStatus> findByHabitAssignId(Long habitAssignId);

    /**
     * Method return {@link HabitStatus} by habitAssignId.
     *
     * @param userId  {@link User} id.
     * @param habitId {@link Habit} id.
     * @return {@link HabitStatus}
     */
    @Query(nativeQuery = true, value = "SELECT * FROM habit_status hs "
        + "INNER JOIN habit_assign ha ON hs.habit_assign_id = ha.id "
        + "WHERE ha.user_id = ?1 AND ha.habit_id = ?2")
    Optional<HabitStatus> findByUserIdAndHabitId(Long userId, Long habitId);

    /**
     * Method delete {@link HabitStatus} by habitAssignId.
     *
     * @param habitAssignId - id of {@link HabitAssign}
     */
    void deleteByHabitAssignId(Long habitAssignId);

    /**
     * Method delete {@link HabitStatus} by userId and habitId.
     *
     * @param userId  {@link User} id.
     * @param habitId {@link Habit} id.
     */
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM habit_status hs "
        + "USING habit_assign ha "
        + "WHERE hs.habit_assign_id = ha.id "
        + "AND ha.user_id = ?1 AND ha.habit_id = ?2")
    void deleteByUserIdAndHabitId(Long userId, Long habitId);
}
