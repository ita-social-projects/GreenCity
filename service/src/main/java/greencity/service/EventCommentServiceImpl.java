package greencity.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.event.EventAuthorDto;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentDto;
import greencity.dto.eventcomment.EventCommentForSendEmailDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventComment;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventCommentRepo;
import greencity.repository.EventRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventCommentServiceImpl implements EventCommentService {
    private EventCommentRepo eventCommentRepo;
    private EventService eventService;
    private ModelMapper modelMapper;
    private final EventRepo eventRepo;
    private final RestClient restClient;

    /**
     * Method to save {@link greencity.entity.event.EventComment}.
     *
     * @param eventId                   id of {@link greencity.entity.event.Event}
     *                                  to which we save comment.
     * @param addEventCommentDtoRequest dto with
     *                                  {@link greencity.entity.event.EventComment}
     *                                  text, parentCommentId.
     * @param userVO                    {@link User} that saves the comment.
     * @return {@link AddEventCommentDtoResponse} instance.
     */
    @Override
    public AddEventCommentDtoResponse save(Long eventId, AddEventCommentDtoRequest addEventCommentDtoRequest,
        UserVO userVO) {
        EventVO eventVO = eventService.findById(eventId);
        EventComment eventComment = modelMapper.map(addEventCommentDtoRequest, EventComment.class);
        eventComment.setUser(modelMapper.map(userVO, User.class));
        eventComment.setEvent(modelMapper.map(eventVO, Event.class));
        AddEventCommentDtoResponse addEventCommentDtoResponse = modelMapper.map(
            eventCommentRepo.save(eventComment), AddEventCommentDtoResponse.class);
        sendEmailDto(addEventCommentDtoResponse);
        return addEventCommentDtoResponse;
    }

    /**
     * Method to send {@link greencity.dto.eventcomment.EventCommentForSendEmailDto}
     * for sending notification to the event organizer about the EventComment
     * addition.
     *
     * @param addEventCommentDtoResponse to get all needed information about
     *                                   EventComment addition.
     */
    @Override
    public void sendEmailDto(AddEventCommentDtoResponse addEventCommentDtoResponse) {
        Long id = addEventCommentDtoResponse.getId();
        EventComment eventComment = eventCommentRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + id));
        Event event = eventComment.getEvent();
        User organizer = event.getOrganizer();
        EventAuthorDto eventAuthorDto = modelMapper.map(organizer, EventAuthorDto.class);

        EventCommentForSendEmailDto dto = EventCommentForSendEmailDto.builder()
            .id(addEventCommentDtoResponse.getId())
            .author(addEventCommentDtoResponse.getAuthor())
            .text(addEventCommentDtoResponse.getText())
            .createdDate(addEventCommentDtoResponse.getCreatedDate())
            .organizer(eventAuthorDto)
            .email(organizer.getEmail())
            .build();
        restClient.sendNewEventComment(dto);
    }

    /**
     * Method to get certain comment to {@link EventVO} specified by commentId.
     *
     * @param id specifies {@link EventCommentDto} to which we search for comments
     * @return comment to certain event specified by commentId.
     */
    @Override
    public EventCommentDto getEventCommentById(Long id) {
        EventComment eventComment = eventCommentRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + id));

        return modelMapper.map(eventComment, EventCommentDto.class);
    }

    /**
     * Method to count not deleted comments to certain {@link Event}.
     *
     * @param eventId to specify {@link Event}
     * @return amount of comments
     */
    @Override
    public int countComments(Long eventId) {
        return eventCommentRepo.countEventCommentsByEvent(eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId)));
    }

    /**
     * Method to get all active comments to {@link Event} specified by eventId.
     *
     * @param pageable page of event.
     * @param eventId  specifies {@link Event} to which we search for comments
     * @return all active comments to certain event specified by eventId.
     */
    @Override
    public PageableDto<EventCommentDto> getAllActiveComments(Pageable pageable, UserVO userVO, Long eventId) {
        Optional<Event> event = eventRepo.findById(eventId);

        if (event.isEmpty()) {
            throw new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId);
        }

        Page<EventComment> pages = eventCommentRepo.findAllByEventIdOrderByCreatedDateDesc(pageable, eventId);
        List<EventCommentDto> eventCommentDto = pages
            .stream()
            .map(eventComment -> modelMapper.map(eventComment, EventCommentDto.class))
            .collect(Collectors.toList());

        return new PageableDto<>(
            eventCommentDto,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages());
    }

    /**
     * Method to delete comment {@link EventComment} by id.
     *
     * @param eventCommentId specifies {@link EventComment} to which we search for
     *                       comments.
     */
    @Override
    public void delete(Long eventCommentId, UserVO user) {
        EventComment eventComment = eventCommentRepo
            .findById(eventCommentId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + eventCommentId));

        if (!user.getId().equals(eventComment.getUser().getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        eventCommentRepo.deleteById(eventCommentId);
    }
}
