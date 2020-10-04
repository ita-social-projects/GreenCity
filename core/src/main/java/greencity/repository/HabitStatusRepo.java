package greencity.repository;

import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatus;
import greencity.entity.User;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitStatusRepo extends JpaRepository<HabitStatus, Long> {
    /**
     * Method to return {@link HabitStatus} by {@link HabitAssign} id.
     *
     * @param habitAssignId - id of {@link HabitAssign}.
     * @return {@link HabitStatus} instance, if it doesn't exist returns Optional.
     */
    @Query(value = "SELECT hs FROM HabitStatus hs "
        + "WHERE hs.habitAssign.id = :habitAssignId")
    Optional<HabitStatus> findByHabitAssignId(Long habitAssignId);

    /**
     * Method to return active {@link HabitStatus} by {@link Habit} id and {@link User} id.
     *
     * @param userId  {@link User} id.
     * @param habitId {@link Habit} id.
     * @return {@link HabitStatus} instance, if it doesn't exist returns Optional.
     */
    @Query(value = "SELECT hs FROM HabitStatus hs "
        + "WHERE hs.habitAssign.user.id = :userId AND hs.habitAssign.habit.id = :habitId "
        + "AND hs.habitAssign.suspended = false")
    Optional<HabitStatus> findByUserIdAndHabitId(@Param("userId") Long userId,
                                                 @Param("habitId") Long habitId);

    /**
     * Method to return {@link HabitStatus} by {@link Habit}, {@link User} id's
     * and {@link ZonedDateTime} dateTime.
     *
     * @param userId  {@link User} id.
     * @param habitId {@link Habit} id.
     * @return {@link HabitStatus} instance, if it doesn't exist returns Optional.
     */
    @Query(value = "SELECT hs FROM HabitStatus hs "
        + "WHERE hs.habitAssign.habit.id = :habitId AND hs.habitAssign.user.id = :userId "
        + "AND DATE(hs.habitAssign.createDate) = :dateTime")
    Optional<HabitStatus> findByUserIdAndHabitIdAndCreateDate(@Param("userId") Long userId,
                                                              @Param("habitId") Long habitId,
                                                              @Param("dateTime") ZonedDateTime dateTime);

    /**
     * Method to delete {@link HabitStatus} by {@link HabitAssign} id.
     *
     * @param habitAssignId - id of {@link HabitAssign}.
     */
    @Modifying
    @Query(value = "DELETE FROM HabitStatus hs "
        + "WHERE hs.habitAssign.id = :habitAssignId")
    void deleteByHabitAssignId(@Param("habitAssignId") Long habitAssignId);

    /**
     * Method to delete {@link HabitStatus} by {@link User} id and {@link Habit} id.
     *
     * @param userId  {@link User} id.
     * @param habitId {@link Habit} id.
     */
    @Modifying
    @Query(value = "DELETE FROM HabitStatus hs "
        + "WHERE hs.habitAssign.user.id = :userId AND hs.habitAssign.habit.id = :habitId "
        + "AND hs.habitAssign.suspended = false")
    void deleteByUserIdAndHabitId(@Param("userId") Long userId, @Param("habitId") Long habitId);

    /**
     * Method delete {@link HabitStatus} by {@link User} id and {@link Habit} id.
     *
     * @param userId  {@link User} id.
     * @param habitId {@link Habit} id.
     */
    @Modifying
    @Query(value = "DELETE FROM HabitStatus hs "
        + "WHERE hs.habitAssign.user.id = :userId AND hs.habitAssign.habit.id = :habitId "
        + "AND DATE(hs.habitAssign.createDate) = :dateTime "
        + "AND hs.habitAssign.suspended = false")
    void deleteByUserIdAndHabitIdAndCreateDate(@Param("userId") Long userId,
                                               @Param("habitId") Long habitId,
                                               @Param("dateTime") ZonedDateTime dateTime);
}
