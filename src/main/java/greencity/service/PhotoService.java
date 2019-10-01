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
    Optional<Photo> findByName(String name);
}
