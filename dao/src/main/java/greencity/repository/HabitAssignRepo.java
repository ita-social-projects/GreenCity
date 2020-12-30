package greencity.repository;

import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import java.time.LocalDate;
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
    @Query(value = "SELECT ha FROM HabitAssign ha"
        + " JOIN FETCH ha.habit h JOIN FETCH h.habitTranslations ht"
        + " JOIN FETCH ht.language l"
        + " WHERE ha.id = :id")
    Optional<HabitAssign> findById(@Param("id") Long id);

    /**
     * Method to find all {@link HabitAssign} by {@link User} id (including
     * suspended).
     *
     * @param userId {@link User} id.
     * @return list of {@link HabitAssign} instances.
     */
    @Query(value = "SELECT DISTINCT ha FROM HabitAssign ha"
        + " JOIN FETCH ha.habit h JOIN FETCH h.habitTranslations ht"
        + " JOIN FETCH ht.language l"
        + " WHERE ha.user.id = :userId")
    List<HabitAssign> findAllByUserId(@Param("userId") Long userId);

    /**
     * Method to find all {@link HabitAssign} by {@link Habit} id (including
     * suspended).
     *
     * @param habitId {@link Habit} id.
     * @return list of {@link HabitAssign} instances.
     */
    @Query(value = "SELECT DISTINCT ha FROM HabitAssign ha"
        + " JOIN FETCH ha.habit h JOIN FETCH h.habitTranslations ht"
        + " JOIN FETCH ht.language l"
        + " WHERE h.id = :habitId")
    List<HabitAssign> findAllByHabitId(@Param("habitId") Long habitId);

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
     * Method to find all {@link HabitAssign}'s by {@link User}.
     *
     * @param userId {@link User} id.
     * @return list of {@link HabitAssign} instances.
     */
    @Query(value = "SELECT DISTINCT ha FROM HabitAssign ha"
        + " JOIN FETCH ha.habit h JOIN FETCH h.habitTranslations ht"
        + " JOIN FETCH ht.language l"
        + " WHERE ha.user.id = :userId AND upper(ha.status) = 'INPROGRESS' OR upper(ha.status) = 'ACQUIRED'")
    List<HabitAssign> findAllByUserIdAndActive(@Param("userId") Long userId);

    /**
     * Method to find all {@link HabitAssign}'s by {@link Habit} id and acquired
     * status (with not suspended status).
     *
     * @param habitId {@link Habit} id.
     * @return list of {@link HabitAssign} instances.
     */
    @Query(value = "SELECT DISTINCT ha FROM HabitAssign ha"
        + " JOIN FETCH ha.habit h JOIN FETCH h.habitTranslations ht"
        + " JOIN FETCH ht.language l"
        + " WHERE h.id = :habitId AND upper(ha.status) = 'INPROGRESS'")
    List<HabitAssign> findAllByHabitIdAndActive(@Param("habitId") Long habitId);

    /**
     * Method to find {@link HabitAssign} by {@link User} id and {@link Habit} id
     * (with not suspended status).
     *
     * @param userId  {@link User} id.
     * @param habitId {@link Habit} id.
     * @return {@link HabitAssign} instance, if it doesn't exist returns Optional.
     */
    @Query(value = "SELECT ha FROM HabitAssign ha"
        + " JOIN FETCH ha.habit h JOIN FETCH h.habitTranslations ht"
        + " JOIN FETCH ht.language l"
        + " WHERE h.id = :habitId AND ha.user.id = :userId")
    Optional<HabitAssign> findByHabitIdAndUserIdAndSuspendedFalse(@Param("habitId") Long habitId,
        @Param("userId") Long userId);

    /**
     * Method for counting all {@link HabitAssign}'s by {@link User} id (with not
     * suspended status).
     *
     * @param userId {@link User} id.
     * @return amount of items in Optional in case of absence such info.
     */
    @Query(value = "SELECT COUNT(ha.id) FROM HabitAssign ha "
        + "WHERE upper(ha.status) <> 'SUSPENDED'"
        + "GROUP BY ha.id")
    int countHabitAssignsByUserIdAndSuspendedFalse(Long userId);

    /**
     * Method for counting all active {@link HabitAssign}'s by {@link User} id (with
     * not suspended and not acquired status).
     *
     * @param userId {@link User} id.
     * @return amount of items in Optional in case of absence such info.
     */

    @Query(value = "SELECT COUNT(ha.id) FROM HabitAssign ha "
        + "WHERE upper(ha.status) = 'INPROGRESS'"
        + "GROUP BY ha.id")
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
        + "WHERE upper(ha.status) <> 'SUSPENDED' "
        + "AND ha.user.id = :userId "
        + "AND ha.createDate > :start AND ha.createDate < :end")
    int countMarkedHabitAssignsByUserIdAndPeriod(@Param("userId") Long userId,
        @Param("start") ZonedDateTime start,
        @Param("end") ZonedDateTime end);

    /**
     * Method to find all active habit assigns on certain {@link LocalDate}.
     *
     * @param userId {@link User} id.
     * @param date   {@link LocalDate} instance.
     * @return list of {@link HabitAssign} instances.
     */
    @Query(value = "SELECT DISTINCT ha FROM HabitAssign ha "
        + "JOIN FETCH ha.habit h JOIN FETCH h.habitTranslations ht "
        + "JOIN FETCH ht.language l "
        + "WHERE upper(ha.status) = 'INPROGRESS'"
        + "AND ha.user.id = :userId "
        + "AND cast(ha.createDate as date) <= cast(:date as date) "
        + "AND cast(ha.createDate as date) + ha.duration >= cast(:date as date)")
    List<HabitAssign> findAllActiveHabitAssignsOnDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    /**
     * Method to find all active habit assigns between 2 {@link LocalDate}s.
     *
     * @param userId {@link User} id.
     * @param from   {@link LocalDate} instance.
     * @param to     {@link LocalDate} instance.
     * @return list of {@link HabitAssign} instances.
     */
    @Query(value = "SELECT DISTINCT ha FROM HabitAssign ha "
        + "JOIN FETCH ha.habit h JOIN FETCH h.habitTranslations ht "
        + "JOIN FETCH ht.language l "
        + "WHERE upper(ha.status) <> 'SUSPENDED' "
        + "AND ha.user.id = :userId "
        + "AND cast(ha.createDate as date) + ha.duration >= cast(:from as date) "
        + "OR cast(ha.createDate as date) BETWEEN cast(:from as date) AND cast(:to as date)")
    List<HabitAssign> findAllActiveHabitAssignsBetweenDates(@Param("userId") Long userId, @Param("from") LocalDate from,
        @Param("to") LocalDate to);
}
