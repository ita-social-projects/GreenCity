package greencity.service;

import greencity.dto.photo.PhotoVO;
import java.util.Optional;

/**
 * Provides the interface to manage {@code Photo} entity.
 *
 * @author Marian Milian
 * @version 1.0
 */
public interface PhotoService {
    /**
     * Method with return {@link Optional} of {@link PhotoVO} by comment id.
     *
     * @param name of {@link PhotoVO}.
     * @return {@link Optional} of {@link PhotoVO} .
     * @author Marian Milian
     */
    Optional<PhotoVO> findByName(String name);
}
