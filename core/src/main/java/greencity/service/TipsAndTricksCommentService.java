package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoRequest;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoResponse;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentDto;
import greencity.entity.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface TipsAndTricksCommentService {

    /**
     * Method to save {@link greencity.entity.TipsAndTricksComment}.
     *
     * @param tipsAndTricksId                   id of {@link greencity.entity.TipsAndTricks} to which we save comment.
     * @param addTipsAndTricksCommentDtoRequest dto with {@link greencity.entity.TipsAndTricksComment} text, parentCommentId.
     * @param user                              {@link User} that saves the comment.
     * @return {@link AddTipsAndTricksCommentDtoResponse} instance.
     */
    AddTipsAndTricksCommentDtoResponse save(Long tipsAndTricksId,
                                            AddTipsAndTricksCommentDtoRequest addTipsAndTricksCommentDtoRequest,
                                            User user);

    /**
     * Method returns all comments to certain tipsAndTricks specified by tipsAndTricksId.
     *
     * @param user            current {@link User}
     * @param tipsAndTricksId specifies {@link greencity.entity.TipsAndTricks} to which we search for comments
     * @return all comments to certain tipsAndTricks specified by tipsAndTricksId.
     */
    PageableDto<TipsAndTricksCommentDto> findAllComments(Pageable pageable, User user, Long tipsAndTricksId);

    /**
     * Method returns all replies to certain comment specified by parentCommentId.
     *
     * @param parentCommentId specifies {@link greencity.entity.TipsAndTricks} to which we search for replies
     * @return all replies to certain comment specified by parentCommentId.
     */
    List<TipsAndTricksCommentDto> findAllReplies(Long parentCommentId);

    /**
     * Method to mark {@link greencity.entity.TipsAndTricksComment} specified by id as deleted.
     *
     * @param id   id of {@link greencity.entity.TipsAndTricksComment} to delete.
     * @param user current {@link User} that wants to delete.
     */
    void deleteById(Long id, User user);

    /**
     * Method to change the existing {@link greencity.entity.TipsAndTricksComment}.
     *
     * @param text new text of {@link greencity.entity.TipsAndTricksComment}.
     * @param id   to specify {@link greencity.entity.TipsAndTricksComment} that user wants to change.
     * @param user current {@link User} that wants to change.
     */
    void update(String text, Long id, User user);

    /**
     * Method to like or dislike {@link greencity.entity.TipsAndTricksComment} specified by id.
     *
     * @param id   of {@link greencity.entity.TipsAndTricksComment} to like/dislike.
     * @param user current {@link User} that wants to like/dislike.
     */
    void like(Long id, User user);

    /**
     * Method returns count of likes to certain {@link greencity.entity.TipsAndTricksComment} specified by id.
     *
     * @param id of {@link greencity.entity.TipsAndTricksComment} to which we get count of likes.
     * @return count of likes to certain {@link greencity.entity.TipsAndTricksComment} specified by id.
     */
    int countLikes(Long id);

    /**
     * Method to count replies to certain {@link greencity.entity.TipsAndTricksComment}.
     *
     * @param id specifies parent comment to all replies
     * @return amount of replies
     */
    int countReplies(Long id);

    /**
     * Method to count comments to certain {@link greencity.entity.TipsAndTricksComment}.
     *
     * @param tipAndTricksId TipAndTricks to all replies
     * @return amount of replies
     */
    int countComments(Long tipAndTricksId);
}
