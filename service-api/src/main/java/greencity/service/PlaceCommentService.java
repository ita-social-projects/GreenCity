package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.comment.AddCommentDto;
import greencity.dto.comment.CommentAdminDto;
import greencity.dto.comment.CommentReturnDto;
import org.springframework.data.domain.Pageable;

/**
 * Provides the interface to manage {@code Comment} entity.
 *
 * @author Marian Milian
 * @version 1.0
 */
public interface PlaceCommentService {
    /**
     * Method witch return comment by id.
     *
     * @param id of search Comment
     * @return {@link CommentReturnDto}.
     */
    CommentReturnDto findById(Long id);

    /**
     * Save Comment by Place id.
     *
     * @param placeId       Place id to witch related.
     * @param addCommentDto DTO witch contain data of.
     * @param email         of User who add comment.
     * @return @{link {@link CommentReturnDto}}.
     * @author Marian Milian.
     */
    CommentReturnDto save(Long placeId, AddCommentDto addCommentDto, String email);

    /**
     * Method witch delete Comment by Id.
     *
     * @param id of delete comment.
     * @author Rostyslav Khasanov.
     */
    void deleteById(Long id);

    /**
     * Method witch return all comments by page.
     *
     * @param pageable pageable configuration.
     * @return {@link PageableDto}
     * @author Rostyslav Khasanov.
     */
    PageableDto<CommentAdminDto> getAllComments(Pageable pageable);
}
