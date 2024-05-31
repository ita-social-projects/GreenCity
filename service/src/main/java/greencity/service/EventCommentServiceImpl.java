package greencity.service;

import greencity.achievement.AchievementCalculation;
import greencity.constant.EmailNotificationMessagesConstants;
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
import greencity.enums.AchievementAction;
import greencity.enums.AchievementCategoryType;
import greencity.enums.CommentStatus;
import greencity.enums.NotificationType;
import greencity.enums.RatingCalculationEnum;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.message.GeneralEmailMessage;
import greencity.rating.RatingCalculation;
import greencity.repository.EventCommentRepo;
import greencity.repository.EventRepo;
import greencity.repository.UserRepo;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventCommentServiceImpl implements EventCommentService {
    private EventCommentRepo eventCommentRepo;
    private EventService eventService;
    private ModelMapper modelMapper;
    private final EventRepo eventRepo;
    private final RatingCalculation ratingCalculation;
    private AchievementCalculation achievementCalculation;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;
    private final UserNotificationService userNotificationService;
    private final UserRepo userRepo;
    private static final String LINK = "/#/events/";
    @Value("${client.address}")
    private String clientAddress;

    public EventCommentServiceImpl(
        EventCommentRepo eventCommentRepo,
        EventService eventService,
        ModelMapper modelMapper,
        EventRepo eventRepo,
        RatingCalculation ratingCalculation,
        AchievementCalculation achievementCalculation,
        SimpMessagingTemplate messagingTemplate,
        NotificationService notificationService,
        UserNotificationService userNotificationService,
        UserRepo userRepo) {
        this.eventCommentRepo = eventCommentRepo;
        this.eventService = eventService;
        this.modelMapper = modelMapper;
        this.eventRepo = eventRepo;
        this.ratingCalculation = ratingCalculation;
        this.achievementCalculation = achievementCalculation;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
        this.userNotificationService = userNotificationService;
        this.userRepo = userRepo;
    }

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
        if (addEventCommentDtoRequest.getParentCommentId() != null
            && addEventCommentDtoRequest.getParentCommentId() > 0) {
            Long parentCommentId = addEventCommentDtoRequest.getParentCommentId();
            EventComment parentEventComment = eventCommentRepo.findById(parentCommentId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + parentCommentId));

            if (parentEventComment.getParentComment() != null) {
                throw new BadRequestException(ErrorMessage.CANNOT_REPLY_THE_REPLY);
            }

            if (!parentEventComment.getEvent().getId().equals(eventId)) {
                String message = ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + parentCommentId
                    + " in event with id: " + eventId;
                throw new NotFoundException(message);
            }
            eventComment.setParentComment(parentEventComment);
            notificationService.sendEmailNotification(GeneralEmailMessage.builder()
                .email(parentEventComment.getUser().getEmail())
                .subject(EmailNotificationMessagesConstants.REPLY_SUBJECT)
                .message(
                    String.format(EmailNotificationMessagesConstants.REPLY_MESSAGE, eventComment.getUser().getName()))
                .build());
            userNotificationService.createNotification(modelMapper.map(parentEventComment.getUser(), UserVO.class),
                userVO, NotificationType.EVENT_COMMENT_REPLY, parentCommentId, parentEventComment.getText(),
                eventId, eventVO.getTitle());
        }

        sendNotificationToTaggedUser(eventId, userVO, addEventCommentDtoRequest.getText());

        eventComment.setStatus(CommentStatus.ORIGINAL);
        AddEventCommentDtoResponse addEventCommentDtoResponse = modelMapper.map(
            eventCommentRepo.save(eventComment), AddEventCommentDtoResponse.class);

        addEventCommentDtoResponse.setAuthor(modelMapper.map(userVO, EventCommentAuthorDto.class));
        ratingCalculation.ratingCalculation(RatingCalculationEnum.COMMENT_OR_REPLY, userVO);
        achievementCalculation.calculateAchievement(userVO,
            AchievementCategoryType.COMMENT_OR_REPLY, AchievementAction.ASSIGN);
        notificationService.sendEmailNotification(GeneralEmailMessage.builder()
            .email(eventVO.getOrganizer().getEmail())
            .subject(EmailNotificationMessagesConstants.EVENT_COMMENTED_SUBJECT)
            .message(String.format(EmailNotificationMessagesConstants.EVENT_COMMENTED_MESSAGE, eventVO.getTitle()))
            .build());
        userNotificationService.createNotification(eventVO.getOrganizer(), userVO, NotificationType.EVENT_COMMENT,
            eventId, eventVO.getTitle());
        return addEventCommentDtoResponse;
    }

    /**
     * Method to get certain comment to {@link EventVO} specified by commentId.
     *
     * @param id specifies {@link EventCommentDto} to which we search for comments
     * @return comment to certain event specified by commentId.
     */
    @Override
    public EventCommentDto getEventCommentById(Long id, UserVO userVO) {
        EventComment eventComment = eventCommentRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + id));

        if (userVO != null) {
            eventComment.setCurrentUserLiked(eventComment.getUsersLiked().stream()
                .anyMatch(u -> u.getId().equals(userVO.getId())));
        }

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
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));
        return eventCommentRepo.countNotDeletedEventCommentsByEvent(event.getId());
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

        Page<EventComment> pages =
            eventCommentRepo.findAllByParentCommentIdIsNullAndEventIdAndStatusNotOrderByCreatedDateDesc(pageable,
                eventId, CommentStatus.DELETED);

        if (userVO != null) {
            pages.forEach(eventComment -> eventComment.setCurrentUserLiked(eventComment.getUsersLiked()
                .stream()
                .anyMatch(u -> u.getId().equals(userVO.getId()))));
        }

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
     * Method to change the existing {@link greencity.entity.EcoNewsComment}.
     *
     * @param commentText new text of {@link greencity.entity.EcoNewsComment}.
     * @param id          to specify {@link greencity.entity.EcoNewsComment} that
     *                    user wants to change.
     * @param userVO      current {@link User} that wants to change.
     */
    @Override
    @Transactional
    public void update(String commentText, Long id, UserVO userVO) {
        EventComment eventComment = eventCommentRepo.findByIdAndStatusNot(id, CommentStatus.DELETED)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));

        if (!userVO.getId().equals(eventComment.getUser().getId())) {
            throw new BadRequestException(ErrorMessage.NOT_A_CURRENT_USER);
        }

        eventComment.setText(commentText);
        eventComment.setStatus(CommentStatus.EDITED);
        eventCommentRepo.save(eventComment);
    }

    /**
     * Method set true for field 'deleted' of the comment {@link EventComment} and
     * its replies by id.
     *
     * @param eventCommentId specifies {@link EventComment} to which we search for
     *                       comments.
     */
    @Transactional
    @Override
    public void delete(Long eventCommentId, UserVO user) {
        EventComment eventComment = eventCommentRepo
            .findByIdAndStatusNot(eventCommentId, CommentStatus.DELETED)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + eventCommentId));

        if (user.getRole() != Role.ROLE_ADMIN && !user.getId().equals(eventComment.getUser().getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        eventComment.setStatus(CommentStatus.DELETED);
        if (eventComment.getComments() != null) {
            eventComment.getComments()
                .forEach(comment -> comment.setStatus(CommentStatus.DELETED));
        }
        ratingCalculation.ratingCalculation(RatingCalculationEnum.UNDO_COMMENT_OR_REPLY, user);
        achievementCalculation.calculateAchievement(user,
            AchievementCategoryType.COMMENT_OR_REPLY, AchievementAction.DELETE);

        eventCommentRepo.save(eventComment);
    }

    /**
     * Method returns all replies to certain comment specified by parentCommentId.
     *
     * @param pageable        page of replies.
     * @param parentCommentId specifies {@link EventComment} to which we search for
     *                        replies
     * @param userVO          current {@link User}
     * @return all replies to certain comment specified by parentCommentId.
     */
    @Override
    public PageableDto<EventCommentDto> findAllActiveReplies(Pageable pageable, Long parentCommentId, UserVO userVO) {
        Page<EventComment> pages =
            eventCommentRepo.findAllByParentCommentIdAndStatusNotOrderByCreatedDateDesc(pageable, parentCommentId,
                CommentStatus.DELETED);

        if (userVO != null) {
            pages.forEach(ec -> ec.setCurrentUserLiked(ec.getUsersLiked().stream()
                .anyMatch(u -> u.getId().equals(userVO.getId()))));
        }

        List<EventCommentDto> eventCommentDtos = pages.stream()
            .map(eventComment -> modelMapper.map(eventComment, EventCommentDto.class))
            .collect(Collectors.toList());

        return new PageableDto<>(
            eventCommentDtos,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages());
    }

    /**
     * Method to count not deleted replies to the certain {@link EventComment}.
     *
     * @param parentCommentId to specify parent comment {@link EventComment}
     * @return amount of replies
     */
    @Override
    public int countAllActiveReplies(Long parentCommentId) {
        if (eventCommentRepo.findByIdAndStatusNot(parentCommentId, CommentStatus.DELETED).isEmpty()) {
            throw new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + parentCommentId);
        }
        return eventCommentRepo.countByParentCommentIdAndStatusNot(parentCommentId, CommentStatus.DELETED);
    }

    /**
     * Method to like or dislike {@link EventComment} specified by id.
     *
     * @param commentId id of {@link EventComment} to like/dislike.
     * @param userVO    current {@link User} that wants to like/dislike.
     */
    @Override
    public void like(Long commentId, UserVO userVO) {
        EventComment comment = eventCommentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + commentId));
        if (comment.getUsersLiked().stream().anyMatch(user -> user.getId().equals(userVO.getId()))) {
            comment.getUsersLiked().removeIf(user -> user.getId().equals(userVO.getId()));
            ratingCalculation.ratingCalculation(RatingCalculationEnum.UNDO_LIKE_COMMENT_OR_REPLY, userVO);
            achievementCalculation.calculateAchievement(userVO,
                AchievementCategoryType.LIKE_COMMENT_OR_REPLY, AchievementAction.DELETE);
            userNotificationService.removeActionUserFromNotification(modelMapper.map(comment.getUser(), UserVO.class),
                userVO, commentId, NotificationType.EVENT_COMMENT_LIKE);
        } else {
            comment.getUsersLiked().add(modelMapper.map(userVO, User.class));
            achievementCalculation.calculateAchievement(userVO,
                AchievementCategoryType.LIKE_COMMENT_OR_REPLY, AchievementAction.ASSIGN);
            ratingCalculation.ratingCalculation(RatingCalculationEnum.LIKE_COMMENT_OR_REPLY, userVO);
            notificationService.sendEmailNotification(GeneralEmailMessage.builder()
                .email(comment.getUser().getEmail())
                .subject(EmailNotificationMessagesConstants.COMMENT_LIKE_SUBJECT)
                .message(String.format(EmailNotificationMessagesConstants.COMMENT_LIKE_MESSAGE,
                    userVO.getName()))
                .build());
            Event event = comment.getEvent();
            userNotificationService.createNotification(modelMapper.map(comment.getUser(), UserVO.class), userVO,
                NotificationType.EVENT_COMMENT_LIKE, commentId, comment.getText(), event.getId(), event.getTitle());
        }
        eventCommentRepo.save(comment);
    }

    /**
     * Method returns count of likes to certain {@link EventComment} specified by
     * id.
     *
     * @param commentId id of {@link EventComment} must be counted.
     * @param userVO    {@link UserVO} user who want to get amount of likes for
     *                  comment.
     *
     * @return amountCommentLikesDto dto with id and count likes for comments.
     */
    @Override
    public AmountCommentLikesDto countLikes(Long commentId, UserVO userVO) {
        EventComment comment = eventCommentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED).orElseThrow(
            () -> new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + commentId));

        boolean isLiked =
            userVO != null && comment.getUsersLiked().stream().anyMatch(u -> u.getId().equals(userVO.getId()));
        return AmountCommentLikesDto.builder()
            .id(comment.getId())
            .userId(userVO == null ? 0 : userVO.getId())
            .isLiked(isLiked)
            .amountLikes(comment.getUsersLiked().size())
            .build();
    }

    /**
     * Method returns count of likes to certain {@link EventComment} specified by
     * id.
     *
     * @param amountCommentLikesDto dto with id and count likes for comments.
     */
    @Override
    @Transactional
    public void eventCommentLikeAndCount(AmountCommentLikesDto amountCommentLikesDto) {
        EventComment comment = eventCommentRepo.findById(amountCommentLikesDto.getId()).orElseThrow(
            () -> new BadRequestException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        boolean isLiked = comment.getUsersLiked().stream().map(User::getId)
            .anyMatch(x -> x.equals(amountCommentLikesDto.getUserId()));
        amountCommentLikesDto.setLiked(isLiked);
        int size = comment.getUsersLiked().size();
        amountCommentLikesDto.setAmountLikes(size);
        messagingTemplate.convertAndSend("/topic/" + amountCommentLikesDto.getId() + "/eventComment",
            amountCommentLikesDto);
    }

    /**
     * Method to send email notification if user tagged in comment.
     *
     * @param eventId {@link Long} event id.
     * @param userVO  {@link UserVO} comment author.
     * @param comment {@link String} comment.
     */
    private void sendNotificationToTaggedUser(Long eventId, UserVO userVO, String comment) {
        Long userId = getUserIdFromComment(comment);
        if (userId != null) {
            User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));
            notificationService.sendEmailNotification(GeneralEmailMessage.builder()
                .email(user.getEmail())
                .subject(String.format(EmailNotificationMessagesConstants.EVENT_TAGGED_SUBJECT, userVO.getName()))
                .message(clientAddress + LINK + eventId)
                .build());
        }
    }

    /**
     * Method to extract user id from comment.
     *
     * @param message comment from {@link AddEventCommentDtoResponse}.
     * @return user id if present or null.
     */
    private Long getUserIdFromComment(String message) {
        String regEx = "data-userid=\"(\\d+)\"";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return Long.valueOf(matcher.group(1));
        }
        return null;
    }
}
