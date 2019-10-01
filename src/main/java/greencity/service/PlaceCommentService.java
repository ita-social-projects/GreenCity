package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.comment.AddCommentDto;
import greencity.dto.comment.CommentDto;
import greencity.dto.comment.CommentReturnDto;
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

    CommentReturnDto findById(Long id);

    CommentReturnDto save(Long placeId, AddCommentDto addCommentDto, String email);

    void deleteById(Long id);
}
