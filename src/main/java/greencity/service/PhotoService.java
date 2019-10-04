package greencity.service;

import greencity.entity.Photo;
import java.util.Optional;

/**
 * Provides the interface to manage {@code Photo} entity.
 *
 * @author Marian Milian
 * @version 1.0
 */
public interface PhotoService {
    /**
     * Method with return {@link Optional} of {@link Photo} by comment id.
     *
     * @param name of {@link Photo}.
     * @return {@link Optional} of {@link Photo} .
     * @author Marian Milian
     */
    Optional<Photo> findByName(String name);
}
