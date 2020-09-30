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
    Optional<List<HabitAssign>> findAllByUserId(Long userId);

    Optional<List<HabitAssign>> findByUserIdAndAcquired(Long userId, boolean acquired);

    Optional<HabitAssign> findByHabitIdAndUserId(Long habitId, Long userId);

    int countHabitAssignsByUserId(Long userId);

    @Query(nativeQuery = true, value = "SELECT count(h) "
        + "FROM habit_assign ha "
        + "INNER JOIN habit_status hs on ha.habit_id = hs.habit_id "
        + "WHERE ha.status = true "
        + "AND ha.users_id = ?1 "
        + "AND ha.create_date < ?2 "
        + "AND hs.create_date > ?2 OR hs.create_date < ?3")
    int countMarkedHabitAssignsByUserIdAndPeriod(Long userId, ZonedDateTime start, ZonedDateTime end);

    void deleteByHabitIdAndUserId(Long habitId, Long userId);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE habit_assign SET acquired = ?2 WHERE id = ?1")
    void updateAcquiredById(Long id, boolean acquired);
}
