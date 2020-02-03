package greencity.repository;

import greencity.entity.Language;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
    /**
     * method, that returns {@link Language} by it's code.
     *
     * @param languageCode code of the language.
     * @return {@link Language} by it's code.
     */
    Optional<Language> findByCode(String languageCode);
}
