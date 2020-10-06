package greencity.repository;

import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitAssignRepo extends JpaRepository<HabitAssign, Long>,
    JpaSpecificationExecutor<HabitAssign> {
    /**
     * Method to find all {@link HabitAssign} by {@link User} id
     * (including suspended).
     *
     * @param userId {@link User} id.
     * @return list of {@link HabitAssign} instances.
     */
    List<HabitAssign> findAllByUserId(Long userId);

    /**
     * Method to find all {@link HabitAssign} by {@link User} id
     * (with not suspended status).
     *
     * @param userId {@link User} id.
     * @return list of {@link HabitAssign} instances.
     */
    List<HabitAssign> findAllByUserIdAndSuspendedFalse(Long userId);

    /**
     * Method to find all {@link HabitAssign} by {@link User} id and acquired status
     * (with not suspended status).
     *
     * @param userId   {@link User} id.
     * @param acquired {@link Boolean} status.
     * @return list of {@link HabitAssign} instances.
     */
    List<HabitAssign> findAllByUserIdAndAcquiredAndSuspendedFalse(Long userId, Boolean acquired);

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
     * Method to find {@link HabitAssign} by {@link User} and {@link Habit} id's
     * and {@link ZonedDateTime} dateTime.
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
     * Method for counting all {@link HabitAssign} by {@link User} id
     * (with not suspended status).
     *
     * @param userId {@link User} id.
     * @return amount of items in Optional in case of absence such info.
     */
    int countHabitAssignsByUserIdAndSuspendedFalse(Long userId);

    /**
     * Method for counting {@link HabitAssign} by {@link User} id and period between start/end {@link ZonedDateTime}
     * (with not suspended status).
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
        + "AND ha.createDate > :start OR ha.createDate < :end")
    int countMarkedHabitAssignsByUserIdAndPeriod(@Param("userId") Long userId,
                                                 @Param("start") ZonedDateTime start,
                                                 @Param("end") ZonedDateTime end);

    /**
     * Method to suspend {@link HabitAssign} by {@link User} id and {@link Habit} id.
     *
     * @param habitId {@link Habit} id.
     * @param userId  {@link User} id.
     */
    @Modifying
    @Query(value = "UPDATE HabitAssign ha "
        + "SET ha.suspended = true "
        + "WHERE ha.habit.id = :habitId AND ha.user.id = :userId "
        + "AND ha.suspended = false")
    void suspendByHabitIdAndUserId(@Param("habitId") Long habitId, @Param("userId") Long userId);

    /**
     * Method to suspend {@link HabitAssign} by it's id.
     *
     * @param habitAssignId {@link HabitAssign} id.
     */
    @Modifying
    @Query(value = "UPDATE HabitAssign ha "
        + "SET ha.suspended = true WHERE ha.id = :habitAssignId")
    void suspendById(@Param("habitAssignId") Long habitAssignId);

    /**
     * Method to delete {@link HabitAssign} by it's id.
     *
     * @param id {@link HabitAssign} id.
     */
    @Modifying
    @Query("DELETE FROM HabitAssign ha WHERE ha.id = :id")
    void deleteById(@Param("id") Long id);

    /**
     * Method to delete {@link HabitAssign} by {@link User} and {@link Habit} id's
     * and {@link ZonedDateTime} dateTime.
     *
     * @param habitId  {@link Habit} id.
     * @param userId   {@link User} id.
     * @param dateTime {@link ZonedDateTime} dateTime.
     */
    @Modifying
    @Query("DELETE FROM HabitAssign ha "
        + "WHERE ha.habit.id = :habitId AND ha.user.id = :userId "
        + "AND DATE(ha.createDate) = :dateTime")
    void deleteByHabitIdAndUserIdAndCreateDate(Long habitId, Long userId, ZonedDateTime dateTime);

    /**
     * Method for updating {@link HabitAssign} with new acquired status.
     *
     * @param id       {@link HabitAssign} id.
     * @param acquired {@link Boolean} status.
     */
    @Modifying
    @Query(value = "UPDATE HabitAssign ha SET ha.acquired = :acquired WHERE ha.id = :id")
    void updateAcquiredById(@Param("id") Long id, @Param("acquired") Boolean acquired);

    /**
     * Method for getting amount of {@link Habit} in progress by {@link User} id (not suspended).
     *
     * @param id {@link User} id.
     * @return amount of habits in progress by {@link User} id.
     * @author Marian Datsko
     */
    @Query(value = "SELECT COUNT(ha.acquired) FROM HabitAssign ha "
        + " WHERE ha.user.id = :userId"
        + " AND ha.acquired = false AND ha.suspended = false")
    Long getAmountOfHabitsInProgressByUserId(@Param("userId") Long id);

    /**
     * Method for getting amount of acquired {@link Habit} by {@link User} id.
     *
     * @param id {@link User} id.
     * @return amount of acquired habits by {@link User} id.
     * @author Marian Datsko
     */
    @Query(value = "SELECT COUNT(ha.acquired) FROM HabitAssign ha "
        + " WHERE ha.user.id = :userId"
        + " AND ha.acquired = false AND ha.suspended = true")
    Long getAmountOfAcquiredHabitsByUserId(@Param("userId") Long id);
}
