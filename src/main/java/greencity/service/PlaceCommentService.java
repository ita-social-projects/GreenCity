package greencity.service;

import greencity.entity.Comment;
import java.util.List;
import java.util.Optional;

/**
 * Provides the interface to manage {@code Comment} entity.
 *
 * @author Marian Milian
 * @version 1.0
 */
public interface PlaceCommentService {
    List<Comment> findAllByPlaceId(Long placeId);

    Optional<Comment> findById(Long id);
}
