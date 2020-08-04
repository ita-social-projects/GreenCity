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
    @Query("SELECT hd "
        + "FROM HabitDictionary hd "
        + "WHERE hd.habit "
        + "IN(SELECT h "
        + "FROM Habit h "
        + "WHERE h.statusHabit = true AND h.users "
        + "IN(SELECT u "
        + "FROM User u "
        + "WHERE u.id = ?1))")
    List<HabitDictionary> findAvailableHabitDictionaryByUser(@Param("user") User user);
}