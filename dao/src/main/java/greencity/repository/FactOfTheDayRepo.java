package greencity.repository;

import greencity.entity.FactOfTheDay;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FactOfTheDayRepo extends JpaRepository<FactOfTheDay, Long> {
    /**
     * Method finds {@link FactOfTheDay} that satisfy search query.
     *
     * @param searchQuery query to search
     * @return pageable of fact of the day
     */
    @Query("SELECT DISTINCT fd FROM FactOfTheDay AS fd JOIN FactOfTheDayTranslation "
        + "AS fdt ON fd.id = fdt.factOfTheDay.id "
        + "WHERE concat(fd.id, '') like lower(CONCAT(:searchQuery, '')) or "
        + "lower(fd.name) like lower(CONCAT('%', :searchQuery, '%')) "
        + "or lower(fdt.content) like lower(CONCAT('%', :searchQuery, '%')) "
        + "or CONCAT(fdt.id,'') like lower(CONCAT(:searchQuery, '')) ")
    Page<FactOfTheDay> searchBy(Pageable pageable, String searchQuery);

    /**
     * Method for getting random {@link FactOfTheDay} by language code. This method
     * use native SQL query to reduce the load on the backend
     *
     * @return {@link FactOfTheDay} in Optional
     * @author Mykola Lehkyi
     */
    @Query(nativeQuery = true, value = "select * from fact_of_the_day as fd "
        + " ORDER BY RANDOM() LIMIT 1 ")
    Optional<FactOfTheDay> getRandomFactOfTheDay();
}
