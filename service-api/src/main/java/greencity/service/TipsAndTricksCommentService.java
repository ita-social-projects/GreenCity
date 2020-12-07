package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoRequest;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoResponse;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentDto;
import greencity.dto.user.UserVO;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface TipsAndTricksCommentService {
    /**
     * Method to save
     * {@link greencity.dto.tipsandtrickscomment.TipsAndTricksCommentVO}.
     *
     * @param tipsAndTricksId                   id of
     *                                          {@link greencity.dto.tipsandtricks.TipsAndTricksVO}
     *                                          to which we save comment.
     * @param addTipsAndTricksCommentDtoRequest dto with
     *                                          {@link greencity.dto.tipsandtrickscomment.TipsAndTricksCommentVO}
     *                                          text, parentCommentId.
     * @param user                              {@link UserVO} that saves the
     *                                          comment.
     * @return {@link AddTipsAndTricksCommentDtoResponse} instance.
     */
    AddTipsAndTricksCommentDtoResponse save(Long tipsAndTricksId,
        AddTipsAndTricksCommentDtoRequest addTipsAndTricksCommentDtoRequest,
        UserVO user);

    void calculateTipsAndTricksComment(UserVO userVO);

    /**
     * Method returns all comments to certain tipsAndTricks specified by
     * tipsAndTricksId.
     *
     * @param user            current {@link UserVO}
     * @param tipsAndTricksId specifies
     *                        {@link greencity.dto.tipsandtricks.TipsAndTricksVO} to
     *                        which we search for comments
     * @return all comments to certain tipsAndTricks specified by tipsAndTricksId.
     */
    PageableDto<TipsAndTricksCommentDto> findAllComments(Pageable pageable, UserVO user, Long tipsAndTricksId);

    /**
     * Method returns all replies to certain comment specified by parentCommentId.
     *
     * @param parentCommentId specifies
     *                        {@link greencity.dto.tipsandtricks.TipsAndTricksVO} to
     *                        which we search for replies
     * @return all replies to certain comment specified by parentCommentId.
     */
    List<TipsAndTricksCommentDto> findAllReplies(Long parentCommentId);

    /**
     * Method to mark
     * {@link greencity.dto.tipsandtrickscomment.TipsAndTricksCommentVO} specified
     * by id as deleted.
     *
     * @param id   id of
     *             {@link greencity.dto.tipsandtrickscomment.TipsAndTricksCommentVO}
     *             to delete.
     * @param user current {@link UserVO} that wants to delete.
     */
    void deleteById(Long id, UserVO user);

    /**
     * Method to change the existing
     * {@link greencity.dto.tipsandtrickscomment.TipsAndTricksCommentVO}.
     *
     * @param text new text of
     *             {@link greencity.dto.tipsandtrickscomment.TipsAndTricksCommentVO}.
     * @param id   to specify
     *             {@link greencity.dto.tipsandtrickscomment.TipsAndTricksCommentVO}
     *             that user wants to change.
     * @param user current {@link UserVO} that wants to change.
     */
    void update(String text, Long id, UserVO user);

    /**
     * Method to like or dislike
     * {@link greencity.dto.tipsandtrickscomment.TipsAndTricksCommentVO} specified
     * by id.
     *
     * @param id   of
     *             {@link greencity.dto.tipsandtrickscomment.TipsAndTricksCommentVO}
     *             to like/dislike.
     * @param user current {@link UserVO} that wants to like/dislike.
     */
    void like(Long id, UserVO user);

    /**
     * Method returns count of likes to certain
     * {@link greencity.dto.tipsandtrickscomment.TipsAndTricksCommentVO} specified
     * by id.
     *
     * @param id of
     *           {@link greencity.dto.tipsandtrickscomment.TipsAndTricksCommentVO}
     *           to which we get count of likes.
     * @return count of likes to certain
     *         {@link greencity.dto.tipsandtrickscomment.TipsAndTricksCommentVO}
     *         specified by id.
     */
    int countLikes(Long id);

    /**
     * Method to count replies to certain
     * {@link greencity.dto.tipsandtrickscomment.TipsAndTricksCommentVO}.
     *
     * @param id specifies parent comment to all replies
     * @return amount of replies
     */
    int countReplies(Long id);

    /**
     * Method to count comments to certain
     * {@link greencity.dto.tipsandtrickscomment.TipsAndTricksCommentVO}.
     *
     * @param tipAndTricksId TipAndTricks to all replies
     * @return amount of replies
     */
    int countComments(Long tipAndTricksId);
}
