package greencity.repository;

import greencity.entity.TitleTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TitleTranslationRepo extends JpaRepository<TitleTranslation, Long> {
}
