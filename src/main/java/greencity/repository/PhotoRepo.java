package greencity.repository;

import greencity.entity.Photo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides an interface to manage {@link Photo} entity.
 */
@Repository
public interface PhotoRepo extends JpaRepository<Photo, Long> {
    /**
     * Method with return {@link Optional} of {@link Photo} by comment id.
     *
     * @param name of {@link Photo}.
     * @return {@link Optional} of {@link Photo} .
     * @author Marian Milian
     */
    Optional<Photo> findByName(String name);
}
