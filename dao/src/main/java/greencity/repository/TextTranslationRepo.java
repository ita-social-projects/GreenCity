package greencity.repository;

import greencity.entity.TextTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TextTranslationRepo extends JpaRepository<TextTranslation, Long> {
}
