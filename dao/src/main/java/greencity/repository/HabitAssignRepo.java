package greencity.repository;

import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import greencity.enums.HabitAssignStatus;
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
        + " JOIN FETCH ha.habit h LEFT JOIN FETCH h.habitTranslations ht"
        + " JOIN FETCH ht.language l"
        + " WHERE ha.id = :id")
    Optional<HabitAssign> findById(@Param("id") Long id);

    /**
     * Method to find all {@link HabitAssign} by {@link User} id (not cancelled and
     * not expired).
     *
     * @param userId {@link User} id.
     * @return list of {@link HabitAssign} instances.
     */
    @Query(value = "SELECT DISTINCT ha FROM HabitAssign ha"
        + " JOIN FETCH ha.habit h JOIN FETCH h.habitTranslations ht"
        + " JOIN FETCH ht.language l"
        + " WHERE ha.user.id = :userId AND upper(ha.status) NOT IN ('CANCELLED','EXPIRED')")
    List<HabitAssign> findAllByUserId(@Param("userId") Long userId);

    /**
     * Method to find all {@link HabitAssign} by {@link Habit} id (not canceled and
     * not expired).
     *
     * @param habitId {@link Habit} id.
     * @return list of {@link HabitAssign} instances.
     */
    @Query(value = "SELECT DISTINCT ha FROM HabitAssign ha"
        + " JOIN FETCH ha.habit h JOIN FETCH h.habitTranslations ht"
        + " JOIN FETCH ht.language l"
        + " WHERE h.id = :habitId AND upper(ha.status) NOT IN ('CANCELLED','EXPIRED')")
    List<HabitAssign> findAllByHabitId(@Param("habitId") Long habitId);

    /**
     * Method to find {@link HabitAssign} by {@link User} and {@link Habit} id's and
     * {@link ZonedDateTime} dateTime. not canceled and not expired
     *
     * @param habitId  {@link Habit} id.
     * @param userId   {@link User} id.
     * @param dateTime {@link ZonedDateTime} dateTime.
     * @return {@link HabitAssign} instance, if it doesn't exist returns Optional.
     */
    @Query("SELECT ha FROM HabitAssign ha "
        + "WHERE ha.habit.id = :habitId AND ha.user.id = :userId "
        + "AND DATE(ha.createDate) = :dateTime AND upper(ha.status) NOT IN ('CANCELLED','EXPIRED')")
    Optional<HabitAssign> findByHabitIdAndUserIdAndCreateDate(@Param("habitId") Long habitId,
        @Param("userId") Long userId,
        @Param("dateTime") ZonedDateTime dateTime);

    /**
     * Method to find {@link HabitAssign}'s by {@link User} and {@link Habit} id's
     * and INPROGRESS status.
     *
     * @param habitId {@link Habit} id.
     * @param userId  {@link User} id.
     * @return {@link HabitAssign} instance.
     */
    @Query(value = "SELECT DISTINCT ha FROM HabitAssign ha"
        + " WHERE ha.habit.id = :habitId AND ha.user.id = :userId AND upper(ha.status) = 'INPROGRESS'")
    Optional<HabitAssign> findByHabitIdAndUserIdAndStatusIsInprogress(@Param("habitId") Long habitId,
        @Param("userId") Long userId);

    /**
     * Method to find {@link HabitAssign}'s by {@link User} and {@link HabitAssign}
     * id and INPROGRESS status.
     *
     * @param habitAssignId {@link Long} id.
     * @param userId        {@link Long} id.
     * @return {@link HabitAssign} instance.
     * @author Anton Bondar
     */
    @Query(value = "SELECT DISTINCT ha FROM HabitAssign ha"
        + " WHERE ha.id = :habitAssignId AND ha.user.id = :userId AND upper(ha.status) = 'INPROGRESS'")
    Optional<HabitAssign> findByHabitAssignIdUserIdAndStatusIsInProgress(@Param("habitAssignId") Long habitAssignId,
        @Param("userId") Long userId);

    /**
     * Method to find {@link HabitAssign}'s by {@link User} id and ACQUIRED status.
     *
     * @param userId {@link User} id.
     * @return {@link HabitAssign} instance.
     */
    @Query(value = "SELECT DISTINCT ha FROM HabitAssign ha"
        + " WHERE ha.user.id = :userId AND upper(ha.status) = 'ACQUIRED'")
    List<HabitAssign> findAllByUserIdAndStatusAcquired(@Param("userId") Long userId);

    /**
     * Method to find {@link HabitAssign}'s by {@link User} id and CANCELLED status.
     *
     * @param userId {@link User} id.
     * @return {@link HabitAssign} instance.
     */
    @Query(value = "SELECT DISTINCT ha FROM HabitAssign ha"
        + " WHERE ha.user.id = :userId AND upper(ha.status) = 'CANCELLED'")
    List<HabitAssign> findAllByUserIdAndStatusIsCancelled(@Param("userId") Long userId);

    /**
     * Method to find {@link HabitAssign}'s by {@link User} and {@link Habit} id's
     * and CANCELLED status.
     *
     * @param habitId {@link Habit} id.
     * @param userId  {@link User} id.
     * @return {@link HabitAssign} instance.
     */
    @Query(value = "SELECT * FROM habit_assign ha"
        + " WHERE habit_id = :habitId AND user_id = :userId AND upper(ha.status) = 'CANCELLED'", nativeQuery = true)
    HabitAssign findByHabitIdAndUserIdAndStatusIsCancelled(@Param("habitId") Long habitId,
        @Param("userId") Long userId);

    /**
     * Method to find {@link HabitAssign} by {@link User} id and {@link Habit} id
     * (with not cancelled and not expired status).
     *
     * @param userId  {@link User} id.
     * @param habitId {@link Habit} id.
     * @return {@link HabitAssign} instance, if it doesn't exist returns Optional.
     */
    @Query(value = "SELECT ha FROM HabitAssign ha"
        + " JOIN FETCH ha.habit h JOIN FETCH h.habitTranslations ht"
        + " JOIN FETCH ht.language l"
        + " WHERE h.id = :habitId AND ha.user.id = :userId AND upper(ha.status) NOT IN ('CANCELLED','EXPIRED')")
    Optional<HabitAssign> findByHabitIdAndUserId(@Param("habitId") Long habitId,
        @Param("userId") Long userId);

    /**
     * Method for counting all inprogress {@link HabitAssign}'s by {@link User} id
     * (with not cancelled and not acquired status).
     *
     * @param userId {@link User} id.
     * @return amount of items in Optional in case of absence such info.
     */
    @Query(value = "SELECT COUNT(ha.id) FROM HabitAssign ha "
        + "WHERE upper(ha.status) = 'INPROGRESS' AND ha.user.id = :userId")
    int countHabitAssignsByUserIdAndAcquiredFalseAndCancelledFalse(@Param("userId") Long userId);

    /**
     * Method for counting {@link HabitAssign} by {@link User} id and period between
     * start/end {@link ZonedDateTime} (with not cancelled and not expired status).
     *
     * @param userId {@link User} id.
     * @param start  {@link ZonedDateTime} start time.
     * @param end    {@link ZonedDateTime} end time.
     * @return amount of items in Optional in case of absence such info.
     */
    @Query(value = "SELECT COUNT(ha) "
        + "FROM HabitAssign ha "
        + "WHERE upper(ha.status) NOT IN ('CANCELLED','EXPIRED') "
        + "AND ha.user.id = :userId "
        + "AND ha.createDate > :start AND ha.createDate < :end")
    int countMarkedHabitAssignsByUserIdAndPeriod(@Param("userId") Long userId,
        @Param("start") ZonedDateTime start,
        @Param("end") ZonedDateTime end);

    /**
     * Method to find all inprogress habit assigns on certain {@link LocalDate}.
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
    List<HabitAssign> findAllInprogressHabitAssignsOnDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    /**
     * Method to find all inprogress, acquired habit assigns between 2
     * {@link LocalDate}s.
     *
     * @param userId {@link User} id.
     * @param from   {@link LocalDate} instance.
     * @param to     {@link LocalDate} instance.
     * @return list of {@link HabitAssign} instances.
     */
    @Query(value = "SELECT DISTINCT ha FROM HabitAssign ha "
        + "JOIN FETCH ha.habit h JOIN FETCH h.habitTranslations ht "
        + "JOIN FETCH ht.language l "
        + "WHERE upper(ha.status) = 'INPROGRESS'"
        + "AND ha.user.id = :userId "
        + "AND cast(ha.createDate as date) + ha.duration >= cast(:from as date) "
        + "OR cast(ha.createDate as date) BETWEEN cast(:from as date) AND cast(:to as date) "
        + "AND ha.user.id = :userId "
        + "AND upper(ha.status) = 'INPROGRESS'")
    List<HabitAssign> findAllHabitAssignsBetweenDates(@Param("userId") Long userId, @Param("from") LocalDate from,
        @Param("to") LocalDate to);

    /**
     * Method to find all inprogress, habit assigns.
     * 
     * @return list of {@link HabitAssign} instances.
     */
    @Query(value = "SELECT DISTINCT ha FROM HabitAssign ha "
        + "JOIN FETCH ha.habit h JOIN FETCH h.habitTranslations ht "
        + "JOIN FETCH ht.language l "
        + "WHERE upper(ha.status) = 'INPROGRESS'")
    List<HabitAssign> findAllInProgressHabitAssigns();

    /**
     * Method to find all habit assigns by status.
     * 
     * @param status {@link HabitAssignStatus} status of habit assign.
     *
     * @return list of {@link HabitAssign} instances.
     * @author Vira Maksymets
     */
    @Query(value = "SELECT DISTINCT ha FROM HabitAssign ha "
        + "JOIN FETCH ha.habit h JOIN FETCH h.habitTranslations ht "
        + "JOIN FETCH ht.language l "
        + "WHERE (upper(ha.status) = :status) AND (ha.habit.id = :habitId)")
    List<HabitAssign> findAllHabitAssignsByStatusAndHabitId(@Param("status") HabitAssignStatus status,
        @Param("habitId") Long habitId);

    /**
     * Method to find amount of users that acquired habit.
     *
     * @param habitId {@link Habit} id.
     *
     * @return Long.
     * @author Oleh Kulbaba
     */
    @Query(value = "SELECT count(ha)"
        + "FROM HabitAssign ha WHERE ha.habit.id = :habitId AND ha.status='ACQUIRED'")
    Long findAmountOfUsersAcquired(@Param("habitId") Long habitId);

    /**
     * Method to find {@link HabitAssign} by {@link HabitAssign} id.
     *
     * @param habitAssignId {@link Long} id.
     * @param userId        {@link Long} id.
     * @return {@link HabitAssign} instance.
     */
    @Query(value = "FROM HabitAssign ha"
        + " JOIN FETCH ha.habit h JOIN FETCH h.habitTranslations ht"
        + " JOIN FETCH ht.language l"
        + " WHERE ha.id = :habitAssignId AND ha.user.id = :userId")
    Optional<HabitAssign> findByHabitAssignIdAndUserId(Long habitAssignId, Long userId);
}
