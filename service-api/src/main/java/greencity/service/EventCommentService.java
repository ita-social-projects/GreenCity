package greencity.service;

import greencity.dto.PageableDto;
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
     * Method to send {@link greencity.dto.eventcomment.EventCommentForSendEmailDto}
     * for sending notification to the event organizer about the EventComment
     * addition.
     *
     * @param addEventCommentDtoResponse to get all needed information about
     *                                   EventComment addition.
     */
    void sendEmailDto(AddEventCommentDtoResponse addEventCommentDtoResponse);

    /**
     * Method to get certain comment to {@link EventVO} specified by commentId.
     *
     * @param id specifies {@link EventCommentDto} to which we search for comments
     * @return comment to certain event specified by commentId.
     * @author Inna Yashna
     */
    EventCommentDto getEventCommentById(Long id);

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
}
