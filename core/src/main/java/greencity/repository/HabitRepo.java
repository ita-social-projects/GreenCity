package greencity.repository;

import greencity.entity.Habit;
import greencity.entity.User;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Provides an interface to manage {@link Habit} entity.
 */
@Repository
public interface HabitRepo extends JpaRepository<Habit, Long> {
    /**
     * Find {@link Habit} by user.
     *
     * @param userId .
     * @return List Habit's.
     * @author Volodymyr Turko
     */
    @Query("SELECT u.habits FROM User u WHERE u.id = ?1")
    Optional<List<Habit>> findAllByUserId(Long userId);

    /**
     * Find {@link Habit} by user and statusHabit > 0.
     *
     * @param userId id current user.
     * @return List {@link Habit}
     */
    @Query("SELECT h FROM Habit h WHERE h.statusHabit = true AND h.users IN(SELECT u FROM User u WHERE u.id = ?1)")
    List<Habit> findByUserIdAndStatusHabit(Long userId);

//    /**
//     * Find habits by userId and HabitDictionary.id.
//     *
//     * @param userId            id current user.
//     * @param habitDictionaryId id {@link HabitDictionary}
//     * @return {@link Habit}
//     */
//    @Query("FROM Habit WHERE user.id = ?1 AND habitDictionary.id = ?2 AND statusHabit = true")
//    Optional<Habit> findByUserIdAndHabitDictionaryId(Long userId, Long habitDictionaryId);

    /**
     * Method update habit by id and statusHabit.
     *
     * @param id     {@link Habit}
     * @param status {@link Habit}
     */
    @Modifying
    @Query("UPDATE Habit SET statusHabit = ?2 WHERE id = ?1")
    void updateHabitStatusById(Long id, boolean status);

    /**
     * method count user habits.
     *
     * @param userId id current user
     * @return count habits by user
     */
    @Query("SELECT us.habits.size "
        + "FROM User us "
        + "WHERE us.id = ?1 AND us.habits IN(SELECT h FROM Habit h WHERE h.statusHabit = true)")
    int countHabitByUserId(Long userId);

    /**
     * counts user habits that were not marked during period between start and end.
     *
     * @param userId id of {@link User}
     * @param start  first day of period
     * @param end    last day of period
     * @return count of user habits that were marked during some period
     */
    @Query("SELECT u.habits.size "
        + "FROM User u "
        + "WHERE u.id = ?1 "
        + "AND u.habits IN(SELECT h FROM Habit h "
        + "WHERE h.statusHabit = true "
        + "AND h.createDate < ?2 "
        + "AND h.id IN(SELECT hs.habit.id "
        + "FROM HabitStatistic hs "
        + "WHERE hs.createdOn > ?2 or hs.createdOn < ?3))")
    int countMarkedHabitsByUserIdByPeriod(Long userId, ZonedDateTime start, ZonedDateTime end);
}