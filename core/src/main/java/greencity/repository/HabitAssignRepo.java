package greencity.repository;

import greencity.entity.HabitAssign;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitAssignRepo extends JpaRepository<HabitAssign, Long> {
    @Query("SELECT u.habitAssigns FROM User u WHERE u.id = ?1")
    Optional<List<HabitAssign>> findAllByUserId(Long userId);

    @Query(nativeQuery = true, value = "SELECT ha FROM habit_assign ha "
        + "WHERE ha.users_id = ?1 AND ha.acquired = ?2")
    Optional<List<HabitAssign>> findByUserIdAndAcquiredStatus(Long userId, boolean acquired);

    @Modifying
    @Query("UPDATE HabitAssign SET acquired = ?2 WHERE id = ?1")
    void updateHabitAssignAcquiredStatusById(Long id, boolean acquired);

    @Query(nativeQuery = true, value = "SELECT count(ha) FROM habit_assign ha "
        + "WHERE ha.users_id = ?1 AND ha.acquired = true")
    int countHabitAssignsByUserId(Long userId);

    @Query(nativeQuery = true, value = "SELECT count(h) "
        + "FROM habit_assign ha "
        + "INNER JOIN habit_status hs on ha.habit_id = hs.habit_id "
        + "WHERE ha.status = true "
        + "AND ha.users_id = ?1 "
        + "AND ha.create_date < ?2 "
        + "AND hs.create_date > ?2 OR hs.create_date < ?3")
    int countMarkedHabitAssignsByUserIdAndPeriod(Long userId, ZonedDateTime start, ZonedDateTime end);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM habit_assign ha"
        + "WHERE ha.user_id = ?2 AND ha.habit_id = ?1")
    void deleteByHabitIdAndUserId(Long habitId, Long userId);

    @Query(nativeQuery = true, value = "SELECT ha FROM habit_assign ha "
        + "WHERE ha.habit_id = ?1 AND ha.user_id = ?2")
    Optional<HabitAssign> findByHabitIdAndUserId(Long habitId, Long userId);
}
