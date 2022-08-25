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
     * @param eventId id of {@link EventVO} to which we save comment.
     * @param addEventCommentDtoRequest dto with {@link EventCommentVO} text, parentCommentId.
     * @param user {@link UserVO} that saves the comment.
     * @return {@link AddEventCommentDtoResponse} instance.
     * @author Inna Yashna
     */
    AddEventCommentDtoResponse save(Long eventId, AddEventCommentDtoRequest addEventCommentDtoRequest,
                                      UserVO user);

    /**
     * Method to count not deleted event comments to certain {@link EventVO}.
     *
     * @param eventId to specify {@link EventVO}
     * @return amount of comments
     * @author Inna Yashna
     */
    int countComments(Long eventId);

    /**
     * Method to get all active comments to {@link EventVO} specified by
     * eventId.
     *
     * @param pageable  page of event.
     * @param eventId specifies {@link EventVO} to which we search for comments
     * @return all active comments to certain event specified by eventId.
     * @author Inna Yashna
     */
    PageableDto<EventCommentDto> getAllActiveComments(Pageable pageable, UserVO user, Long eventId);
}
