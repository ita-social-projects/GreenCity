package greencity.repository;

import greencity.entity.localization.AchievementTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import greencity.entity.User;

import java.util.List;

public interface AchievementTranslationRepo extends JpaRepository<AchievementTranslation, Long> { }
