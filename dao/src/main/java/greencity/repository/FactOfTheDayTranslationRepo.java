package greencity.repository;

import greencity.entity.FactOfTheDayTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactOfTheDayTranslationRepo extends JpaRepository<FactOfTheDayTranslation, Long> {
}
