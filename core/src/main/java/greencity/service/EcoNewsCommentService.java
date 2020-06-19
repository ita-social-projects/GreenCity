package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoResponse;
import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoRequest;
import greencity.entity.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface EcoNewsCommentService {
    /**
     * Method to save {@link greencity.entity.EcoNewsComment}.
     *
     * @param econewsId id of {@link greencity.entity.EcoNews} to which we save comment.
     * @param addEcoNewsCommentDtoRequest dto with {@link greencity.entity.EcoNewsComment} text, parentCommentId.
     * @param user {@link User} that saves the comment.
     * @return {@link AddEcoNewsCommentDtoResponse} instance.
     */
    AddEcoNewsCommentDtoResponse save(Long econewsId, AddEcoNewsCommentDtoRequest addEcoNewsCommentDtoRequest,
                                      User user);

    /**
     * Method returns all comments to certain ecoNews specified by ecoNewsId.
     *
     * @param user current {@link User}
     * @param ecoNewsId specifies {@link greencity.entity.EcoNews} to which we search for comments
     * @return all comments to certain ecoNews specified by ecoNewsId.
     */
    PageableDto<EcoNewsCommentDto> findAllComments(Pageable pageable, User user, Long ecoNewsId);

    /**
     * Method returns all replies to certain comment specified by parentCommentId.
     *
     * @param parentCommentId specifies {@link greencity.entity.EcoNewsComment} to which we search for replies
     * @param user current {@link User}
     * @return all replies to certain comment specified by parentCommentId.
     */
    List<EcoNewsCommentDto> findAllReplies(Long parentCommentId, User user);

    /**
     * Method to delete {@link greencity.entity.EcoNewsComment} specified by id.
     *
     * @param id id of {@link greencity.entity.EcoNewsComment} to delete.
     * @param user current {@link User} that wants to delete.
     */
    void deleteById(Long id, User user);

    /**
     * Method to change the existing {@link greencity.entity.EcoNewsComment}.
     *
     * @param text new text of {@link greencity.entity.EcoNewsComment}.
     * @param id to specify {@link greencity.entity.EcoNewsComment} that user wants to change.
     * @param user current {@link User} that wants to change.
     */
    void update(String text, Long id, User user);

    /**
     * Method to like or dislike {@link greencity.entity.EcoNewsComment} specified by id.
     *
     * @param id of {@link greencity.entity.EcoNewsComment} to like/dislike.
     * @param user current {@link User} that wants to like/dislike.
     */
    void like(Long id, User user);

    /**
     * Method returns count of likes to certain {@link greencity.entity.EcoNewsComment} specified by id.
     *
     * @param id of {@link greencity.entity.EcoNewsComment} to which we get count of likes.
     * @return count of likes to certain {@link greencity.entity.EcoNewsComment} specified by id.
     */
    int countLikes(Long id);

    /**
     * Method to count replies to certain {@link greencity.entity.EcoNewsComment}.
     *
     * @param id specifies parent comment to all replies
     * @return amount of replies
     */
    int countReplies(Long id);
}
