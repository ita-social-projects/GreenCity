package greencity.repository;

import greencity.entity.FactOfTheDayTranslation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FactOfTheDayTranslationRepo extends JpaRepository<FactOfTheDayTranslation, Long> {
    /**
     * Method for getting random FactOfTheDayTranslation by language code.
     * This method use native SQL query to reduce the load on the backend
     *
     * @param languageCode of Language
     * @return {@link FactOfTheDayTranslation} in Optional
     * @author Mykola Lehkyi
     */
    @Query(nativeQuery = true, value = "select *"
        + " from fact_of_the_day_translations as fdt"
        + " join languages as l on l.id = fdt.language_id"
        + " where l.code = 'en' ORDER BY RANDOM() LIMIT 1")
    Optional<FactOfTheDayTranslation> getRandomFactOfTheDayTranslation(@Param("languageCode") String languageCode);
}
