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
import org.springframework.data.domain.Pageable;

public interface EcoNewsCommentService {
    /**
     * Method to save {@link EcoNewsCommentVO}.
     *
     * @param econewsId                   id of {@link EcoNewsVO} to which we save
     *                                    comment.
     * @param addEcoNewsCommentDtoRequest dto with {@link EcoNewsCommentVO} text,
     *                                    parentCommentId.
     * @param user                        {@link UserVO} that saves the comment.
     * @return {@link AddEcoNewsCommentDtoResponse} instance.
     */
    AddEcoNewsCommentDtoResponse save(Long econewsId, AddEcoNewsCommentDtoRequest addEcoNewsCommentDtoRequest,
        UserVO user);

    /**
     * Method returns all comments to certain ecoNews specified by ecoNewsId.
     *
     * @param user      current {@link UserVO}
     * @param ecoNewsId specifies {@link UserVO} to which we search for comments
     * @return all comments to certain ecoNews specified by ecoNewsId.
     */
    PageableDto<EcoNewsCommentDto> findAllComments(Pageable pageable, UserVO user, Long ecoNewsId);

    /**
     * Method returns all replies to certain comment specified by parentCommentId.
     *
     * @param parentCommentId specifies {@link EcoNewsCommentVO} to which we search
     *                        for replies
     * @param user            current {@link UserVO}
     * @return all replies to certain comment specified by parentCommentId.
     */
    PageableDto<EcoNewsCommentDto> findAllReplies(Pageable pageable, Long parentCommentId, UserVO user);

    /**
     * Method to mark {@link EcoNewsCommentVO} specified by id as deleted.
     *
     * @param id   id of {@link EcoNewsCommentVO} to delete.
     * @param user current {@link UserVO} that wants to delete.
     */
    void deleteById(Long id, UserVO user);

    /**
     * Method to update the text of an existing {@link EcoNewsCommentVO}.
     *
     * @param text new text for {@link EcoNewsCommentVO}.
     * @param id   to specify {@link EcoNewsCommentVO} that user wants to change.
     * @param user current {@link UserVO} that wants to change.
     */
    void update(String text, Long id, UserVO user);

    /**
     * Method to like or dislike {@link EcoNewsCommentVO} specified by id.
     *
     * @param id   of {@link EcoNewsCommentVO} to like/dislike.
     * @param user current {@link UserVO} that wants to like/dislike.
     */
    void like(Long id, UserVO user);

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
     * @param id specifies parent comment to all replies
     * @return amount of replies
     */
    int countReplies(Long id);

    /**
     * Method to count not deleted eco news comments to certain {@link EcoNewsVO}.
     *
     * @param ecoNewsId to specify {@link EcoNewsVO}
     * @return amount of comments
     */
    int countOfComments(Long ecoNewsId);

    /**
     * Method to get all active comments to {@link EcoNewsVO} specified by
     * ecoNewsId.
     *
     * @param pageable  page of news.
     * @param ecoNewsId specifies {@link EcoNewsVO} to which we search for comments
     * @return all active comments to certain ecoNews specified by ecoNewsId.
     * @author Taras Dovganyuk
     */
    PageableDto<EcoNewsCommentDto> getAllActiveComments(Pageable pageable, UserVO user, Long ecoNewsId);

    /**
     * Method returns all active replies to certain comment specified by
     * parentCommentId.
     *
     * @param parentCommentId specifies {@link EcoNewsCommentVO} to which we search
     *                        for replies
     * @param user            current {@link UserVO}
     * @return all replies to certain comment specified by parentCommentId.
     * @author Taras Dovganyuk
     */
    PageableDto<EcoNewsCommentDto> findAllActiveReplies(Pageable pageable, Long parentCommentId, UserVO user);

    /**
     * Method that allow you to search users by name.
     *
     * @param searchUsers dto with current user ID and search query
     *                    {@link UserSearchDto}.
     *
     * @author Anton Bondar
     */
    void searchUsers(UserSearchDto searchUsers);
}
