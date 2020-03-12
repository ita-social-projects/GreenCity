package greencity.repository;

import greencity.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepo extends JpaRepository<Tag, Long> {
    /**
     * method, that returns {@link Tag} by it`s name.
     *
     * @param name name of Tag.
     * @return {@link Tag} by it's name.
     */
    Optional<Tag> findByName(String name);
}
