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
    @Query(nativeQuery = true, value = "SELECT h FROM habits h "
        + "INNER JOIN habits_users_assign hua on h.id = hua.habit_id "
        + "WHERE hua.users_id = ?1 AND h.status = true")
    List<Habit> findByUserIdAndStatusHabit(Long userId);

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
    @Query(nativeQuery = true, value = "SELECT count(h) FROM habits h "
        + "INNER JOIN habits_users_assign hua on h.id = hua.habit_id "
        + "WHERE hua.users_id = ?1 AND h.status = true")
    int countHabitByUserId(Long userId);

    /**
     * counts user habits that were not marked during period between start and end.
     *
     * @param userId id of {@link User}
     * @param start  first day of period
     * @param end    last day of period
     * @return count of user habits that were marked during some period
     */
    @Query(nativeQuery = true, value = "SELECT count(h) "
        + "FROM habits h "
        + "INNER JOIN habits_users_assign hua on h.id = hua.habit_id "
        + "INNER JOIN habit_status hs on h.id = hs.habit_id "
        + "WHERE h.status = true "
        + "AND hua.users_id = ?1 "
        + "AND h.create_date < ?2 "
        + "AND hs.create_date > ?2 OR hs.create_date < ?3")
    int countMarkedHabitsByUserIdByPeriod(Long userId, ZonedDateTime start, ZonedDateTime end);
}