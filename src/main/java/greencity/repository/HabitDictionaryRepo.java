package greencity.repository;

import greencity.entity.HabitDictionary;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides an interface to manage {@link HabitDictionary} entity.
 */
public interface HabitDictionaryRepo extends JpaRepository<HabitDictionary, Long> {
    /**
     * Method with return {@link Optional} of {@link HabitDictionary} by comment id.
     *
     * @param name of {@link HabitDictionary}.
     * @return {@link Optional} of {@link HabitDictionary}.
     */
    Optional<HabitDictionary> findByName(String name);
}

