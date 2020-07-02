package greencity.repository;

import greencity.entity.Tag;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepo extends JpaRepository<Tag, Long> {
    /**
     * method, that returns {@link Tag} by it`s name.
     *
     * @param name name of Tag.
     * @return {@link Tag} by it's name.
     */
    Optional<Tag> findByNameIgnoreCase(String name);
}
