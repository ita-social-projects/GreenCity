package greencity.repository;

import greencity.entity.HabitDictionary;
import greencity.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Provides an interface to manage {@link HabitDictionary} entity.
 * @author Bogdan Kuzenko
 */
public interface HabitDictionaryRepo extends JpaRepository<HabitDictionary, Long> {
    /**
     * Method returns available habit dictionary for specific user.
     *
     * @param user user which we use to filter.
     * @return List of available {@link HabitDictionary}`s.
     */
    @Query("SELECT hd FROM HabitDictionary hd WHERE hd.id NOT IN (SELECT h.habitDictionary "
        + "FROM Habit h WHERE h.user =:user AND h.statusHabit = true)")
    List<HabitDictionary> findAvailableHabitDictionaryByUser(@Param("user") User user);
}

