package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.econewscomment.AmountCommentLikesDto;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentDto;
import greencity.dto.eventcomment.EventCommentVO;
import greencity.dto.user.UserVO;
import greencity.enums.CommentStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface EventCommentService {
    /**
     * Method to save {@link EventCommentVO}.
     *
     * @param eventId                   id of {@link EventVO} to which we save
     *                                  comment.
     * @param addEventCommentDtoRequest dto with {@link EventCommentVO} text,
     *                                  parentCommentId.
     * @param user                      {@link UserVO} that saves the comment.
     * @return {@link AddEventCommentDtoResponse} instance.
     * @author Inna Yashna
     */
    AddEventCommentDtoResponse save(Long eventId, AddEventCommentDtoRequest addEventCommentDtoRequest,
        UserVO user);

    /**
     * Method to get certain comment to {@link EventVO} specified by commentId.
     *
     * @param eventId   id of {@link EventVO} to which we get comment.
     * @param commentId specifies {@link EventCommentDto} to which we search for
     *                  comments
     * @param userVO    {@link UserVO} that get the comment.
     * @return comment to certain event specified by commentId.
     */
    EventCommentDto getEventCommentById(Long eventId, Long commentId, UserVO userVO);

    /**
     * Method to count not deleted event comments to certain {@link EventVO}.
     *
     * @param eventId to specify {@link EventVO}
     * @return amount of comments
     * @author Inna Yashna
     */
    int countComments(Long eventId);

    /**
     * Method to get all active comments to {@link EventVO} specified by eventId.
     *
     * @param pageable page of event.
     * @param user     {@link UserVO} that get the comments.
     * @param eventId  specifies {@link EventVO} to which we search for comments
     * @param statuses statuses of comments.
     * @return all active comments to certain event specified by eventId.
     */
    PageableDto<EventCommentDto> getAllComments(Pageable pageable, UserVO user, Long eventId,
        List<CommentStatus> statuses);

    /**
     * Method to change the existing {@link EventCommentVO}.
     *
     * @param commentText new text of {@link EventCommentVO}.
     * @param eventId     event id,
     * @param commentId   to specify {@link EventCommentVO} that user wants to
     *                    change.
     * @param user        current {@link UserVO} that wants to change.
     */
    void update(String commentText, Long eventId, Long commentId, UserVO user);

    /**
     * Method for deleting the {@link EventCommentVO} instance by its id.
     *
     * @param eventId        event id where located comment.
     * @param eventCommentId - {@link EventCommentVO} instance id which will be
     *                       deleted.
     * @param user           current {@link EventCommentVO} that wants to delete.
     */
    void delete(Long eventId, Long eventCommentId, UserVO user);

    /**
     * Method to get replies by statuses for to certain {@link EventCommentVO}
     * specified by id.
     *
     * @param pageable        page of replies.
     * @param eventId         event where located replies.
     * @param parentCommentId to specify {@link EventCommentVO}.
     * @param statuses        statuses of comment.
     * @param user            {@link UserVO} that want to get replies.
     * @return replies for comment
     */
    PageableDto<EventCommentDto> findAllReplies(Pageable pageable, Long eventId, Long parentCommentId,
        List<CommentStatus> statuses, UserVO user);

    /**
     * Method to count not deleted replies for to certain {@link EventCommentVO}
     * specified by id.
     *
     * @param eventId         event id.
     * @param parentCommentId to specify {@link EventCommentVO}.
     * @return amount of replies.
     */
    int countAllActiveReplies(Long eventId, Long parentCommentId);

    /**
     * Method to like or dislike {@link EventCommentVO} specified by id.
     *
     * @param eventId   event id.
     * @param commentId id of {@link EventCommentVO} to like/dislike.
     * @param userVO    current {@link UserVO} that wants to like/dislike.
     */
    void like(Long eventId, Long commentId, UserVO userVO);

    /**
     * Method returns count of likes to certain {@link EventCommentVO} specified by
     * id.
     *
     * @param eventId   event id.
     * @param commentId id of {@link EventCommentVO} must be counted.
     * @param userVO    {@link UserVO} user who want to get amount of likes for
     *                  comment.
     * @return amountCommentLikesDto dto with id and count likes for comments.
     */
    AmountCommentLikesDto countLikes(Long eventId, Long commentId, UserVO userVO);

    /**
     * Method returns count of likes to certain {@link EventCommentVO} specified by
     * id.
     *
     * @param amountCommentLikesDto {@link AmountCommentLikesDto} dto with id and
     *                              count likes for comments.
     */
    void eventCommentLikeAndCount(AmountCommentLikesDto amountCommentLikesDto);
}
