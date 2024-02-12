package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.econewscomment.AmountCommentLikesDto;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentDto;
import greencity.dto.eventcomment.EventCommentVO;
import greencity.dto.user.UserVO;
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
     * @param id specifies {@link EventCommentDto} to which we search for comments
     * @return comment to certain event specified by commentId.
     * @author Inna Yashna
     */
    EventCommentDto getEventCommentById(Long id, UserVO userVO);

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
     * @param eventId  specifies {@link EventVO} to which we search for comments
     * @return all active comments to certain event specified by eventId.
     * @author Inna Yashna
     */
    PageableDto<EventCommentDto> getAllActiveComments(Pageable pageable, UserVO user, Long eventId);

    /**
     * Method to change the existing {@link EventCommentVO}.
     *
     * @param commentText new text of {@link EventCommentVO}.
     * @param id          to specify {@link EventCommentVO} that user wants to
     *                    change.
     * @param user        current {@link UserVO} that wants to change.
     */
    void update(String commentText, Long id, UserVO user);

    /**
     * Method for deleting the {@link EventCommentVO} instance by its id.
     *
     * @param eventCommentId - {@link EventCommentVO} instance id which will be
     *                       deleted.
     * @param user           current {@link EventCommentVO} that wants to delete.
     */
    void delete(Long eventCommentId, UserVO user);

    /**
     * Method to get all not deleted replies for to certain {@link EventCommentVO}
     * specified by id.
     *
     * @param parentCommentId to specify {@link EventCommentVO}.
     * @param user            {@link UserVO} that want to get replies.
     * @return replies for comment
     */
    PageableDto<EventCommentDto> findAllActiveReplies(Pageable pageable, Long parentCommentId, UserVO user);

    /**
     * Method to count not deleted replies for to certain {@link EventCommentVO}
     * specified by id.
     *
     * @param parentCommentId to specify {@link EventCommentVO}.
     * @return amount of replies.
     */
    int countAllActiveReplies(Long parentCommentId);

    /**
     * Method to like or dislike {@link EventCommentVO} specified by id.
     *
     * @param commentId id of {@link EventCommentVO} to like/dislike.
     * @param userVO    current {@link UserVO} that wants to like/dislike.
     */
    void like(Long commentId, UserVO userVO);

    /**
     * Method returns count of likes to certain {@link EventCommentVO} specified by
     * id.
     *
     * @param commentId id of {@link EventCommentVO} must be counted.
     * @param userVO    {@link UserVO} user who want to get amount of likes for
     *                  comment.
     *
     * @return amountCommentLikesDto dto with id and count likes for comments.
     */
    AmountCommentLikesDto countLikes(Long commentId, UserVO userVO);

    /**
     * Method returns count of likes to certain {@link EventCommentVO} specified by
     * id.
     *
     * @param amountCommentLikesDto {@link AmountCommentLikesDto} dto with id and
     *                              count likes for comments.
     */
    void eventCommentLikeAndCount(AmountCommentLikesDto amountCommentLikesDto);
}
