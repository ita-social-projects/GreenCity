package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.comment.CommentDto;
import greencity.entity.Comment;
import java.security.Principal;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

/**
 * Provides the interface to manage {@code Comment} entity.
 *
 * @author Marian Milian
 * @version 1.0
 */
public interface PlaceCommentService {
    PageableDto<Comment> findAllByPlaceId(Long placeId, Pageable pageable);

    Optional<Comment> findById(Long id);

    Comment save(Long placeId, CommentDto commentDto, String email);

    Long deleteById(Long id);
}
