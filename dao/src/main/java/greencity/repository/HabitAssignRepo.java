package greencity.repository;

import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitAssignRepo extends JpaRepository<HabitAssign, Long>,
    JpaSpecificationExecutor<HabitAssign> {
    /**
     * Method to find {@link HabitAssign} by id.
     *
     * @param id {@link HabitAssign} id.
     * @return {@link HabitAssign} instance.
     */
    @Query(value = "select ha from HabitAssign ha"
        + " join fetch ha.habit h left join fetch h.habitTranslations ht join fetch ht.language l"
        + " where ha.id = :id")
    Optional<HabitAssign> findById(@Param("id") Long id);

    /**
     * Method to find all {@link HabitAssign} by {@link User} id (including
     * suspended).
     *
     * @param userId {@link User} id.
     * @return list of {@link HabitAssign} instances.
     */
    List<HabitAssign> findAllByUserId(Long userId);

    /**
     * Method to find all {@link HabitAssign} by {@link Habit} id (including
     * suspended).
     *
     * @param habitId {@link Habit} id.
     * @return list of {@link HabitAssign} instances.
     */
    List<HabitAssign> findAllByHabitId(Long habitId);

    /**
     * Method to find {@link HabitAssign} by {@link User} and {@link Habit} id's and
     * {@link ZonedDateTime} dateTime.
     *
     * @param habitId  {@link Habit} id.
     * @param userId   {@link User} id.
     * @param dateTime {@link ZonedDateTime} dateTime.
     * @return {@link HabitAssign} instance, if it doesn't exist returns Optional.
     */
    @Query("SELECT ha FROM HabitAssign ha "
        + "WHERE ha.habit.id = :habitId AND ha.user.id = :userId "
        + "AND DATE(ha.createDate) = :dateTime")
    Optional<HabitAssign> findByHabitIdAndUserIdAndCreateDate(@Param("habitId") Long habitId,
        @Param("userId") Long userId,
        @Param("dateTime") ZonedDateTime dateTime);

    /**
     * Method to find all {@link HabitAssign}'s by {@link User} id and acquired
     * status (with not suspended status).
     *
     * @param userId   {@link User} id.
     * @param acquired {@link Boolean} status.
     * @return list of {@link HabitAssign} instances.
     */
    List<HabitAssign> findAllByUserIdAndAcquiredAndSuspendedFalse(Long userId, Boolean acquired);

    /**
     * Method to find all {@link HabitAssign}'s by {@link Habit} id and acquired
     * status (with not suspended status).
     *
     * @param habitId  {@link Habit} id.
     * @param acquired {@link Boolean} status.
     * @return list of {@link HabitAssign} instances.
     */
    List<HabitAssign> findAllByHabitIdAndAcquiredAndSuspendedFalse(Long habitId, Boolean acquired);

    /**
     * Method to find {@link HabitAssign} by {@link User} id and {@link Habit} id
     * (with not suspended status).
     *
     * @param userId  {@link User} id.
     * @param habitId {@link Habit} id.
     * @return {@link HabitAssign} instance, if it doesn't exist returns Optional.
     */
    Optional<HabitAssign> findByHabitIdAndUserIdAndSuspendedFalse(Long habitId, Long userId);

    /**
     * Method to find {@link HabitAssign} by it's id (with not suspended status).
     *
     * @param id {@link HabitAssign} id.
     * @return {@link HabitAssign} instance, if it doesn't exist returns Optional.
     */
    Optional<HabitAssign> findByIdAndSuspendedFalse(Long id);

    /**
     * Method for counting all {@link HabitAssign}'s by {@link User} id (with not
     * suspended status).
     *
     * @param userId {@link User} id.
     * @return amount of items in Optional in case of absence such info.
     */
    int countHabitAssignsByUserIdAndSuspendedFalse(Long userId);

    /**
     * Method for counting all active {@link HabitAssign}'s by {@link User} id (with
     * not suspended and not acquired status).
     *
     * @param userId {@link User} id.
     * @return amount of items in Optional in case of absence such info.
     */
    int countHabitAssignsByUserIdAndSuspendedFalseAndAcquiredFalse(Long userId);

    /**
     * Method for counting {@link HabitAssign} by {@link User} id and period between
     * start/end {@link ZonedDateTime} (with not suspended status).
     *
     * @param userId {@link User} id.
     * @param start  {@link ZonedDateTime} start time.
     * @param end    {@link ZonedDateTime} end time.
     * @return amount of items in Optional in case of absence such info.
     */
    @Query(value = "SELECT COUNT(ha) "
        + "FROM HabitAssign ha "
        + "WHERE ha.suspended = false "
        + "AND ha.user.id = :userId "
        + "AND ha.createDate > :start AND ha.createDate < :end")
    int countMarkedHabitAssignsByUserIdAndPeriod(@Param("userId") Long userId,
        @Param("start") ZonedDateTime start,
        @Param("end") ZonedDateTime end);
}
