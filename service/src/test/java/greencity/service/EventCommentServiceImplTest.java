package greencity.service;

import greencity.ModelUtils;
import greencity.achievement.AchievementCalculation;
import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.econewscomment.AmountCommentLikesDto;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentAuthorDto;
import greencity.dto.eventcomment.EventCommentDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventComment;
import greencity.enums.CommentStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.message.GeneralEmailMessage;
import greencity.rating.RatingCalculation;
import greencity.repository.EventCommentRepo;
import greencity.repository.EventRepo;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import static greencity.ModelUtils.getAmountCommentLikesDto;
import static greencity.ModelUtils.getUser;
import static greencity.ModelUtils.getUserVO;
import static greencity.ModelUtils.getEventComment;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class EventCommentServiceImplTest {
    @Mock
    private EventCommentRepo eventCommentRepo;
    @Mock
    private EventService eventService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    EventRepo eventRepo;
    @Mock
    RestClient restClient;
    @InjectMocks
    private EventCommentServiceImpl eventCommentService;
    @Mock
    private UserService userService;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    private RatingCalculation ratingCalculation;
    @Mock
    private AchievementCalculation achievementCalculation;
    @Mock
    private UserRepo userRepo;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserNotificationService userNotificationService;

    @Test
    void save() {
        UserVO userVO = getUserVO();
        User user = getUser();
        EventVO eventVO = ModelUtils.getEventVO();
        Event event = ModelUtils.getEvent();
        AddEventCommentDtoRequest addEventCommentDtoRequest = ModelUtils.getAddEventCommentDtoRequest();
        EventComment eventComment = getEventComment();
        EventCommentAuthorDto eventCommentAuthorDto = ModelUtils.getEventCommentAuthorDto();

        when(eventService.findById(anyLong())).thenReturn(eventVO);
        when(eventCommentRepo.save(any(EventComment.class))).then(AdditionalAnswers.returnsFirstArg());
        when(eventCommentRepo.findById(anyLong())).thenReturn(Optional.of(eventComment));
        when(modelMapper.map(userVO, EventCommentAuthorDto.class)).thenReturn(eventCommentAuthorDto);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(modelMapper.map(eventVO, Event.class)).thenReturn(event);
        when(modelMapper.map(addEventCommentDtoRequest, EventComment.class)).thenReturn(eventComment);
        when(modelMapper.map(any(EventComment.class), eq(AddEventCommentDtoResponse.class)))
            .thenReturn(ModelUtils.getAddEventCommentDtoResponse());
        when(modelMapper.map(eventComment.getUser(), UserVO.class)).thenReturn(userVO);

        eventCommentService.save(1L, addEventCommentDtoRequest, userVO);
        assertEquals(CommentStatus.ORIGINAL, eventComment.getStatus());
        verify(eventCommentRepo).save(any(EventComment.class));
    }

    @Test
    void sendNotificationIfUserTaggedInComment() {
        UserVO userVO = getUserVO();
        User user = getUser();
        EventVO eventVO = ModelUtils.getEventVO();
        Event event = ModelUtils.getEvent();
        AddEventCommentDtoRequest addEventCommentDtoRequest = AddEventCommentDtoRequest.builder()
            .text("<a contenteditable=\"false\" data-userid=\"5\" style=\"font-weight: 700;\">@Dmytro</a> test")
            .build();
        EventComment eventComment = getEventComment();
        EventCommentAuthorDto eventCommentAuthorDto = ModelUtils.getEventCommentAuthorDto();

        when(eventService.findById(anyLong())).thenReturn(eventVO);
        when(eventCommentRepo.save(any(EventComment.class))).then(AdditionalAnswers.returnsFirstArg());
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(User.builder()
            .id(5L)
            .email("test@email.com")
            .build()));
        when(modelMapper.map(userVO, EventCommentAuthorDto.class)).thenReturn(eventCommentAuthorDto);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(modelMapper.map(eventVO, Event.class)).thenReturn(event);
        when(modelMapper.map(addEventCommentDtoRequest, EventComment.class)).thenReturn(eventComment);
        when(modelMapper.map(any(EventComment.class), eq(AddEventCommentDtoResponse.class)))
            .thenReturn(ModelUtils.getAddEventCommentDtoResponse());

        eventCommentService.save(1L, addEventCommentDtoRequest, userVO);

        verify(notificationService, times(2)).sendEmailNotification(any(GeneralEmailMessage.class));
    }

    @Test
    void saveReplyWithWrongParentIdThrowException() {
        Long parentCommentId = 123L;
        UserVO userVO = getUserVO();
        User user = getUser();
        EventVO eventVO = ModelUtils.getEventVO();
        Event event = ModelUtils.getEvent();
        AddEventCommentDtoRequest addEventCommentDtoRequest = ModelUtils.getAddEventCommentDtoRequest();
        addEventCommentDtoRequest.setParentCommentId(parentCommentId);
        EventComment eventComment = getEventComment();

        when(eventService.findById(anyLong())).thenReturn(eventVO);
        when(eventCommentRepo.findById(parentCommentId)).thenReturn(Optional.empty());
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(modelMapper.map(eventVO, Event.class)).thenReturn(event);
        when(modelMapper.map(addEventCommentDtoRequest, EventComment.class)).thenReturn(eventComment);

        NotFoundException notFoundException =
            assertThrows(NotFoundException.class,
                () -> eventCommentService.save(1L, addEventCommentDtoRequest, userVO));

        assertEquals(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + parentCommentId, notFoundException.getMessage());
    }

    @Test
    void saveReplyWithWrongEventIdThrowException() {
        Long parentCommentId = 123L;
        Long replyEventId = 1L;
        UserVO userVO = getUserVO();
        User user = getUser();
        AddEventCommentDtoRequest addEventCommentDtoRequest = ModelUtils.getAddEventCommentDtoRequest();
        addEventCommentDtoRequest.setParentCommentId(parentCommentId);
        EventComment eventComment = getEventComment();
        EventVO eventVO = ModelUtils.getEventVO();
        Event event = ModelUtils.getEvent();
        event.setId(replyEventId);
        Event parentCommentEvent = ModelUtils.getEvent();
        parentCommentEvent.setId(2L);
        EventComment parentEventComment = getEventComment();
        parentEventComment.setId(parentCommentId);
        parentEventComment.setEvent(parentCommentEvent);

        when(eventService.findById(anyLong())).thenReturn(eventVO);
        when(eventCommentRepo.findById(parentCommentId)).thenReturn(Optional.of(parentEventComment));
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(modelMapper.map(eventVO, Event.class)).thenReturn(event);
        when(modelMapper.map(addEventCommentDtoRequest, EventComment.class)).thenReturn(eventComment);

        NotFoundException notFoundException =
            assertThrows(NotFoundException.class,
                () -> eventCommentService.save(replyEventId, addEventCommentDtoRequest, userVO));

        String expectedErrorMessage = ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + parentCommentId
            + " in event with id: " + event.getId();
        assertEquals(expectedErrorMessage, notFoundException.getMessage());
    }

    @Test
    void saveReplyForReplyThrowException() {
        Long parentCommentId = 123L;
        Long replyEventId = 1L;
        UserVO userVO = getUserVO();
        User user = getUser();
        AddEventCommentDtoRequest addEventCommentDtoRequest = ModelUtils.getAddEventCommentDtoRequest();
        addEventCommentDtoRequest.setParentCommentId(parentCommentId);

        EventComment eventComment = getEventComment();
        EventVO eventVO = ModelUtils.getEventVO();

        Event event = ModelUtils.getEvent();
        event.setId(replyEventId);

        EventComment parentEventComment = getEventComment();
        parentEventComment.setId(parentCommentId);
        parentEventComment.setEvent(event);
        parentEventComment.setParentComment(getEventComment());

        when(eventService.findById(anyLong())).thenReturn(eventVO);
        when(eventCommentRepo.findById(parentCommentId)).thenReturn(Optional.of(parentEventComment));
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(modelMapper.map(eventVO, Event.class)).thenReturn(event);
        when(modelMapper.map(addEventCommentDtoRequest, EventComment.class)).thenReturn(eventComment);

        BadRequestException badRequestException =
            assertThrows(BadRequestException.class,
                () -> eventCommentService.save(replyEventId, addEventCommentDtoRequest, userVO));

        String expectedErrorMessage = ErrorMessage.CANNOT_REPLY_THE_REPLY;

        assertEquals(expectedErrorMessage, badRequestException.getMessage());
    }

    @Test
    void getEventCommentById() {
        EventComment eventComment = getEventComment();
        EventCommentDto eventCommentDto = modelMapper.map(eventComment, EventCommentDto.class);
        when(eventCommentRepo.findById(1L)).thenReturn(Optional.of(eventComment));
        assertEquals(eventCommentDto, eventCommentService.getEventCommentById(1L, getUserVO()));
    }

    @Test
    void countComments() {
        Event event = ModelUtils.getEvent();
        Long eventId = 1L;

        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        when(eventCommentRepo.countNotDeletedEventCommentsByEvent(eventId)).thenReturn(1);

        assertEquals(1, eventCommentService.countComments(eventId));
    }

    @Test
    void countCommentsEventNotFoundException() {
        Long eventId = 1L;
        when(eventRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> eventCommentService.countComments(eventId));
    }

    @Test
    void getAllActiveComments() {
        int pageNumber = 1;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        UserVO userVO = getUserVO();
        Long eventId = 1L;
        EventComment eventComment = getEventComment();
        Event event = ModelUtils.getEvent();
        Page<EventComment> pages = new PageImpl<>(Collections.singletonList(eventComment), pageable, 1);
        EventCommentDto eventCommentDto = ModelUtils.getEventCommentDto();

        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        when(eventCommentRepo.findAllByParentCommentIdIsNullAndEventIdAndStatusNotOrderByCreatedDateDesc(pageable,
            eventId, CommentStatus.DELETED))
            .thenReturn(pages);
        when(modelMapper.map(eventComment, EventCommentDto.class)).thenReturn(eventCommentDto);

        PageableDto<EventCommentDto> allComments = eventCommentService.getAllActiveComments(pageable, userVO, eventId);
        assertEquals(eventCommentDto, allComments.getPage().get(0));
        assertEquals(4, allComments.getTotalElements());
        assertEquals(1, allComments.getCurrentPage());
        assertEquals(1, allComments.getPage().size());
    }

    @Test
    void getAllActiveCommentsEventNotFoundException() {
        Long eventId = 1L;
        when(eventRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> eventCommentService.countComments(eventId));
    }

    @Test
    void update() {
        UserVO userVO = getUserVO();
        Long commentId = 1L;
        String editedText = "edited text";
        EventComment eventComment = getEventComment();

        when(eventCommentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED))
            .thenReturn(Optional.ofNullable(eventComment));

        eventCommentService.update(editedText, commentId, userVO);

        assertEquals(CommentStatus.EDITED, eventComment.getStatus());
        verify(eventCommentRepo).save(any(EventComment.class));
    }

    @Test
    void updateCommentThatDoesntExistsThrowException() {
        UserVO userVO = getUserVO();
        Long commentId = 1L;
        String editedText = "edited text";

        when(eventCommentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)).thenReturn(Optional.empty());

        NotFoundException notFoundException =
            assertThrows(NotFoundException.class,
                () -> eventCommentService.update(editedText, commentId, userVO));
        assertEquals(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION, notFoundException.getMessage());
    }

    @Test
    void updateCommentThatDoesntBelongsToUserThrowException() {
        User user = ModelUtils.getUser();
        UserVO userVO = getUserVO();
        user.setId(2L);

        Long commentId = 1L;
        EventComment eventComment = getEventComment();
        eventComment.setUser(user);
        String editedText = "edited text";

        when(eventCommentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED))
            .thenReturn(Optional.of(eventComment));

        BadRequestException badRequestException =
            assertThrows(BadRequestException.class,
                () -> eventCommentService.update(editedText, commentId, userVO));
        assertEquals(ErrorMessage.NOT_A_CURRENT_USER, badRequestException.getMessage());
    }

    @Test
    void delete() {
        UserVO userVO = getUserVO();
        Long commentId = 1L;
        EventComment eventComment = getEventComment();
        when(eventCommentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED))
            .thenReturn(Optional.ofNullable(eventComment));
        eventCommentService.delete(commentId, userVO);

        assertEquals(CommentStatus.DELETED, eventComment.getComments().get(0).getStatus());
        assertEquals(CommentStatus.DELETED, eventComment.getStatus());
        verify(eventCommentRepo).findByIdAndStatusNot(any(Long.class), eq(CommentStatus.DELETED));
    }

    @Test
    void deleteCommentUserHasNoPermissionThrowException() {
        Long commentId = 1L;

        User user = getUser();
        user.setId(2L);

        UserVO userToDeleteVO = getUserVO();

        EventComment eventComment = getEventComment();
        eventComment.setUser(user);

        when(eventCommentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED))
            .thenReturn(Optional.of(eventComment));

        UserHasNoPermissionToAccessException noPermissionToAccessException =
            assertThrows(UserHasNoPermissionToAccessException.class,
                () -> eventCommentService.delete(commentId, userToDeleteVO));
        assertEquals(ErrorMessage.USER_HAS_NO_PERMISSION, noPermissionToAccessException.getMessage());
    }

    @Test
    void deleteCommentThatDoesntExistsThrowException() {
        UserVO userVO = getUserVO();
        Long commentId = 1L;

        when(eventCommentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)).thenReturn(Optional.empty());

        NotFoundException notFoundException =
            assertThrows(NotFoundException.class, () -> eventCommentService.delete(commentId, userVO));
        assertEquals(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + commentId, notFoundException.getMessage());
    }

    @Test
    void findAllActiveRepliesTest() {
        int pageNumber = 1;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        UserVO userVO = getUserVO();
        Long parentCommentId = 1L;

        EventComment childComment = getEventComment();
        childComment.setParentComment(getEventComment());

        Page<EventComment> page = new PageImpl<>(Collections.singletonList(childComment), pageable, 1);

        when(modelMapper.map(childComment, EventCommentDto.class)).thenReturn(ModelUtils.getEventCommentDto());
        when(eventCommentRepo.findAllByParentCommentIdAndStatusNotOrderByCreatedDateDesc(pageable, parentCommentId,
            CommentStatus.DELETED))
            .thenReturn(page);

        PageableDto<EventCommentDto> eventCommentDtos =
            eventCommentService.findAllActiveReplies(pageable, parentCommentId, userVO);
        assertEquals(getEventComment().getId(), eventCommentDtos.getPage().get(0).getId());
        assertEquals(4, eventCommentDtos.getTotalElements());
        assertEquals(1, eventCommentDtos.getCurrentPage());
        assertEquals(1, eventCommentDtos.getPage().size());
    }

    @Test
    void findAllActiveRepliesCurrentUserLikedTest() {
        int pageNumber = 1;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        UserVO userVO = getUserVO();
        User user = getUser();
        Long parentCommentId = 1L;

        EventComment childComment = getEventComment();
        childComment.setParentComment(getEventComment());
        childComment.setUsersLiked(new HashSet<>(Collections.singletonList(user)));

        Page<EventComment> page = new PageImpl<>(Collections.singletonList(childComment), pageable, 1);

        when(eventCommentRepo.findAllByParentCommentIdAndStatusNotOrderByCreatedDateDesc(pageable, parentCommentId,
            CommentStatus.DELETED))
            .thenReturn(page);

        eventCommentService.findAllActiveReplies(pageable, parentCommentId, userVO);

        assertTrue(childComment.isCurrentUserLiked());
    }

    @Test
    void countAllActiveRepliesTest() {
        Long parentCommentId = 1L;
        int repliesAmount = 5;
        when(eventCommentRepo.findByIdAndStatusNot(parentCommentId, CommentStatus.DELETED))
            .thenReturn(Optional.of(getEventComment()));
        when(eventCommentRepo.countByParentCommentIdAndStatusNot(parentCommentId, CommentStatus.DELETED))
            .thenReturn(repliesAmount);

        int result = eventCommentService.countAllActiveReplies(parentCommentId);
        assertEquals(repliesAmount, result);
    }

    @Test
    void countAllActiveRepliesNotFoundParentCommentTest() {
        Long parentCommentId = 1L;
        when(eventCommentRepo.findByIdAndStatusNot(parentCommentId, CommentStatus.DELETED))
            .thenReturn(Optional.empty());
        NotFoundException notFoundException =
            assertThrows(NotFoundException.class, () -> eventCommentService.countAllActiveReplies(parentCommentId));

        assertEquals(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + parentCommentId, notFoundException.getMessage());
    }

    @Test
    void likeTest() {
        Long commentId = 1L;
        UserVO userVO = getUserVO();
        User user = getUser();
        EventComment comment = getEventComment();

        when(eventCommentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)).thenReturn(Optional.of(comment));
        when(modelMapper.map(userVO, User.class)).thenReturn(user);

        eventCommentService.like(commentId, userVO);

        assertTrue(comment.getUsersLiked().contains(user));
    }

    @Test
    void unLikeTest() {
        Long commentId = 1L;
        UserVO userVO = getUserVO();
        User user = getUser();
        EventComment comment = getEventComment();
        comment.setCurrentUserLiked(true);
        comment.getUsersLiked().add(user);
        when(eventCommentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)).thenReturn(Optional.of(comment));

        eventCommentService.like(commentId, userVO);

        assertFalse(comment.getUsersLiked().contains(user));
    }

    @Test
    void likeNotFoundCommentTest() {
        Long commentId = 1L;
        UserVO userVO = getUserVO();

        when(eventCommentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)).thenReturn(Optional.empty());

        NotFoundException notFoundException =
            assertThrows(NotFoundException.class, () -> eventCommentService.like(commentId, userVO));

        assertEquals(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + commentId, notFoundException.getMessage());
    }

    @Test
    void countLikesTest() {
        Long commentId = 1L;
        UserVO userVO = getUserVO();
        EventComment comment = getEventComment();
        Integer usersLikedAmount = 5;

        for (long i = 0; i < usersLikedAmount; i++) {
            User user = getUser();
            user.setId(i);
            comment.getUsersLiked().add(user);
        }

        when(eventCommentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)).thenReturn(Optional.of(comment));

        AmountCommentLikesDto result = eventCommentService.countLikes(commentId, userVO);

        assertEquals(commentId, result.getId());
        assertEquals(comment.getUser().getId(), result.getUserId());
        assertEquals(usersLikedAmount, result.getAmountLikes());
        assertTrue(result.isLiked());
    }

    @Test
    void countLikesNotFoundCommentTest() {
        Long commentId = 1L;
        UserVO userVO = getUserVO();

        when(eventCommentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)).thenReturn(Optional.empty());

        NotFoundException notFoundException =
            assertThrows(NotFoundException.class, () -> eventCommentService.countLikes(commentId, userVO));

        assertEquals(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + commentId, notFoundException.getMessage());
    }

    @Test
    void eventCommentLikeAndCountTest() {
        var amountCommentLikesDto = getAmountCommentLikesDto();

        EventComment eventComment = getEventComment();
        eventComment.setUsersLiked(new HashSet<>());

        when(eventCommentRepo.findById(amountCommentLikesDto.getId())).thenReturn(Optional.of(eventComment));
        doNothing().when(messagingTemplate).convertAndSend("/topic/"
            + amountCommentLikesDto.getId() + "/eventComment", amountCommentLikesDto);

        eventCommentService.eventCommentLikeAndCount(amountCommentLikesDto);

        verify(eventCommentRepo).findById(1L);
        verify(messagingTemplate).convertAndSend("/topic/"
            + amountCommentLikesDto.getId() + "/eventComment", amountCommentLikesDto);
    }

    @Test
    void eventCommentLikeAndCountThatDoesntExistsThrowBadRequestExceptionTest() {
        var amountCommentLikesDto = getAmountCommentLikesDto();

        when(eventCommentRepo.findById(amountCommentLikesDto.getId())).thenReturn(Optional.empty());

        BadRequestException badRequestException =
            assertThrows(BadRequestException.class,
                () -> eventCommentService.eventCommentLikeAndCount(amountCommentLikesDto));

        assertEquals(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION, badRequestException.getMessage());
        verify(eventCommentRepo).findById(amountCommentLikesDto.getId());
    }
}
