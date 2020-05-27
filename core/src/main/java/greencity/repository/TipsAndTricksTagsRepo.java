package greencity.repository;

import greencity.entity.TipsAndTricksTag;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipsAndTricksTagsRepo extends JpaRepository<TipsAndTricksTag, Long> {
    /**
     * method, that returns {@link TipsAndTricksTag} by it`s name.
     *
     * @param name name of Tag.
     * @return {@link TipsAndTricksTag} by it's name.
     */
    Optional<TipsAndTricksTag> findByName(String name);
}
