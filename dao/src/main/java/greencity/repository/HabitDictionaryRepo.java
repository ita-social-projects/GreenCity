package greencity.repository;

import greencity.entity.HabitDictionary;
import greencity.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Provides an interface to manage {@link HabitDictionary} entity.
 *
 * @author Bogdan Kuzenko
 */
public interface HabitDictionaryRepo extends JpaRepository<HabitDictionary, Long> {
    /**
     * Method returns available habit dictionary for specific user.
     *
     * @param user user which we use to filter.
     * @return List of available {@link HabitDictionary}`s.
     */
    @Query(nativeQuery = true, value = "SELECT hd "
        + "FROM habit_dictionary hd "
        + "INNER JOIN habits hb "
        + "ON hb.habit_dictionary_id = hd.id "
        + "INNER habits_users_assign hua "
        + "ON hua.habit_id = hb.id "
        + "AND hua.users_id = ?1")
    List<HabitDictionary> findAvailableHabitDictionaryByUser(@Param("user") User user);
}