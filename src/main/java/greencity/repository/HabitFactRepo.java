package greencity.repository;

import greencity.entity.HabitFact;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitFactRepo extends JpaRepository<HabitFact, Long> {
    /**
     * Method for getting random habit fact by habit id.
     * This method use native SQL query to reduce the load on the backend
     *
     * @param habitId Id of HabitDictionary
     * @return {@link HabitFact} in Optional
     * @author Vitaliy Dzen
     */
    @Query(nativeQuery = true, value = "SELECT * FROM habit_facts WHERE habit_dictionary_id = ?1 "
        + "ORDER BY RANDOM() LIMIT 1;")
    Optional<HabitFact> getRandomHabitFactByHabitId(Long habitId);

    /**
     * Method find {@link HabitFact} by fact.
     *
     * @param fact of {@link HabitFact}
     * @return {@link HabitFact} in Optional
     * @author Vitaliy Dzen
     */
    Optional<HabitFact> findHabitFactByFact(String fact);
}
