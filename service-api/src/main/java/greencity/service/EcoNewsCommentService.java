package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoRequest;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoResponse;
import greencity.dto.econewscomment.AmountCommentLikesDto;
import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.user.UserSearchDto;
import greencity.dto.user.UserVO;
import greencity.enums.CommentStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface EcoNewsCommentService {
    /**
     * Method to save {@link EcoNewsCommentVO}.
     *
     * @param ecoNewsId                   id of {@link EcoNewsVO} to which we save
     *                                    comment.
     * @param addEcoNewsCommentDtoRequest dto with {@link EcoNewsCommentVO} text,
     *                                    parentCommentId.
     * @param user                        {@link UserVO} that saves the comment.
     * @return {@link AddEcoNewsCommentDtoResponse} instance.
     */
    AddEcoNewsCommentDtoResponse save(Long ecoNewsId, AddEcoNewsCommentDtoRequest addEcoNewsCommentDtoRequest,
        UserVO user);

    /**
     * Method returns replies to certain comment specified by parentCommentId and
     * statuses.
     *
     * @param parentCommentId specifies {@link EcoNewsCommentVO} to which we search
     *                        for replies
     * @param statuses        statuses of comment
     * @param user            current {@link UserVO}
     * @return all replies to certain comment specified by parentCommentId.
     */
    PageableDto<EcoNewsCommentDto> findAllReplies(Pageable pageable, Long ecoNewsId, Long parentCommentId,
        List<CommentStatus> statuses,
        UserVO user);

    /**
     * Method to mark {@link EcoNewsCommentVO} specified by id as deleted.
     *
     * @param commentId id of {@link EcoNewsCommentVO} to delete.
     * @param user      current {@link UserVO} that wants to delete.
     */
    void deleteById(Long ecoNewsId, Long commentId, UserVO user);

    /**
     * Method to update the text of an existing {@link EcoNewsCommentVO}.
     *
     * @param text new text for {@link EcoNewsCommentVO}.
     * @param id   to specify {@link EcoNewsCommentVO} that user wants to change.
     * @param user current {@link UserVO} that wants to change.
     */
    void update(Long ecoNewsId, String text, Long id, UserVO user);

    /**
     * Method to like or remove like from {@link EcoNewsCommentVO} specified by id.
     *
     * @param commentId of {@link EcoNewsCommentVO} to like/dislike.
     * @param user      current {@link UserVO} that wants to like/dislike.
     */
    void like(Long ecoNewsId, Long commentId, UserVO user);

    /**
     * Method returns count of likes to certain {@link EcoNewsCommentVO} specified
     * by id.
     *
     * @param amountCommentLikesDto dto with id and count likes for comments.
     */
    void countLikes(AmountCommentLikesDto amountCommentLikesDto);

    /**
     * Method to count replies to certain {@link EcoNewsCommentVO}.
     *
     * @param commentId specifies parent comment to all replies
     * @return amount of replies
     */
    int countReplies(Long ecoNewsId, Long commentId);

    /**
     * Method to count not deleted eco news comments to certain {@link EcoNewsVO}.
     *
     * @param ecoNewsId to specify {@link EcoNewsVO}
     * @return amount of comments
     */
    int countOfComments(Long ecoNewsId);

    /**
     * Method to get all comments to {@link EcoNewsVO} specified by ecoNewsId and
     * statuses.
     *
     * @param pageable  page of news.
     * @param ecoNewsId specifies {@link EcoNewsVO} to which we search for comments
     * @param statuses  statuses of comment
     * @return all active comments to certain ecoNews specified by ecoNewsId.
     * @author Taras Dovganyuk
     */
    PageableDto<EcoNewsCommentDto> findAllComments(Pageable pageable, UserVO user, Long ecoNewsId,
        List<CommentStatus> statuses);

    /**
     * Method that allow you to search users by name.
     *
     * @param searchUsers dto with current user ID and search query
     *                    {@link UserSearchDto}.
     * @author Anton Bondar
     */
    void searchUsers(UserSearchDto searchUsers);
}
