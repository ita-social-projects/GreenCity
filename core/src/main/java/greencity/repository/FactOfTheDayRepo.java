package greencity.repository;

import greencity.entity.FactOfTheDay;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactOfTheDayRepo extends JpaRepository<FactOfTheDay, Long> {
    /**
     * Method finds all{@link FactOfTheDay} with specified title.
     *
     * @param title of {@link FactOfTheDay}
     * @return List of {@link FactOfTheDay}
     */
    List<FactOfTheDay> findAllByTitle(String title);
}
