package greencity.repository;

import greencity.entity.Advice;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdviceRepo extends JpaRepository<Advice, Long> {
    /**
     * Method for getting random name by habit id.
     * This method use native SQL query to reduce the load on the backend
     *
     * @param habitId Id of HabitDictionary
     * @return Advice Object in Optional
     * @author Vitaliy Dzen
     */
    @Query(nativeQuery = true, value = "SELECT * FROM advices WHERE habit_dictionary_id = ?1 ORDER BY RAND() LIMIT 1;")
    Optional<Advice> getRandomAdviceByHabitId(Long habitId);

    /**
     * Method find {@link Advice} by name.
     *
     * @param name of {@link Advice}
     * @return {@link Advice} in Optional
     * @author Vitaliy Dzen
     */
    Optional<Advice> findAdviceByName(String name);
}
