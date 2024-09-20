package greencity.service;

import greencity.achievement.AchievementCalculation;
import greencity.constant.EmailNotificationMessagesConstants;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.comment.AmountCommentLikesDto;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentAuthorDto;
import greencity.dto.eventcomment.EventCommentDto;
import greencity.dto.eventcomment.EventCommentVO;
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
import greencity.mapping.EventCommentVOMapper;
import greencity.message.GeneralEmailMessage;
import greencity.message.UserTaggedInCommentMessage;
import greencity.rating.RatingCalculation;
import greencity.repository.EventCommentRepo;
import greencity.repository.EventRepo;
import greencity.repository.UserRepo;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventCommentServiceImpl implements EventCommentService {
    private final EventCommentRepo eventCommentRepo;
    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final EventCommentVOMapper eventCommentVOMapper;
    private final EventRepo eventRepo;
    private final RatingCalculation ratingCalculation;
    private final AchievementCalculation achievementCalculation;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;
    private final UserNotificationService userNotificationService;
    private final UserRepo userRepo;
    @Value("${client.address}")
    private String clientAddress;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public AddEventCommentDtoResponse save(Long eventId, AddEventCommentDtoRequest addEventCommentDtoRequest,
        UserVO userVO, Locale locale) {
        EventComment eventComment = modelMapper.map(addEventCommentDtoRequest, EventComment.class);
        EventVO eventVO = eventService.findById(eventId);
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
            userNotificationService.createNotification(modelMapper.map(parentEventComment.getUser(), UserVO.class),
                userVO, NotificationType.EVENT_COMMENT_REPLY, parentCommentId, parentEventComment.getText(),
                eventId, eventVO.getTitle());
        }
        eventComment.setStatus(CommentStatus.ORIGINAL);
        AddEventCommentDtoResponse addEventCommentDtoResponse = modelMapper.map(
            eventCommentRepo.save(eventComment), AddEventCommentDtoResponse.class);
        addEventCommentDtoResponse.setAuthor(modelMapper.map(userVO, EventCommentAuthorDto.class));
        EventCommentVO eventCommentVO = eventCommentVOMapper.convert(eventComment);
        sendNotificationToTaggedUser(eventVO, userVO, eventCommentVO, locale);
        ratingCalculation.ratingCalculation(RatingCalculationEnum.COMMENT_OR_REPLY, userVO);
        achievementCalculation.calculateAchievement(userVO,
            AchievementCategoryType.COMMENT_OR_REPLY, AchievementAction.ASSIGN);
        userNotificationService.createNotification(eventVO.getOrganizer(), userVO, NotificationType.EVENT_COMMENT,
            eventId, eventVO.getTitle(), eventComment.getId(), eventComment.getText());
        return addEventCommentDtoResponse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventCommentDto getEventCommentById(Long eventId, Long commentId, UserVO userVO) {
        UserVO user = userVO == null ? UserVO.builder().build() : userVO;
        return eventCommentRepo.findById(commentId)
            .filter(c -> c.getEvent().getId().equals(eventId))
            .map(c -> setCurrentUserLikedToComment(c, user))
            .map(c -> modelMapper.map(c, EventCommentDto.class))
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + commentId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countComments(Long eventId) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));
        return eventCommentRepo.countNotDeletedEventCommentsByEvent(event.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<EventCommentDto> getAllComments(Pageable pageable, UserVO userVO, Long eventId,
        List<CommentStatus> statuses) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));

        Page<EventComment> pages = eventCommentRepo
            .findAllByEventIdAndParentCommentIsNullAndStatusInOrderByCreatedDateDesc(pageable, event.getId(), statuses);

        UserVO user = userVO == null ? UserVO.builder().build() : userVO;
        List<EventCommentDto> eventCommentDtos = convertToEventCommentDtos(pages, user);

        return new PageableDto<>(
            eventCommentDtos,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void update(String commentText, Long eventId, Long commentId, UserVO userVO, Locale locale) {
        EventComment eventComment = eventCommentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)
            .filter(c -> c.getEvent().getId().equals(eventId))
            .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        if (!userVO.getId().equals(eventComment.getUser().getId())) {
            throw new BadRequestException(ErrorMessage.NOT_A_CURRENT_USER);
        }
        eventComment.setText(commentText);
        eventComment.setStatus(CommentStatus.EDITED);
        eventCommentRepo.save(eventComment);
        EventVO eventVO = eventService.findById(eventComment.getEvent().getId());
        EventCommentVO commentVO = modelMapper.map(eventComment, EventCommentVO.class);
        sendNotificationToTaggedUser(eventVO, userVO, commentVO, locale);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void delete(Long eventId, Long eventCommentId, UserVO user) {
        EventComment eventComment = eventCommentRepo.findByIdAndStatusNot(eventCommentId, CommentStatus.DELETED)
            .filter(c -> c.getEvent().getId().equals(eventId))
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
     * {@inheritDoc}
     */
    @Override
    public PageableDto<EventCommentDto> findAllReplies(Pageable pageable, Long eventId, Long parentCommentId,
        List<CommentStatus> statuses, UserVO userVO) {
        Page<EventComment> pages = eventCommentRepo
            .findAllByParentCommentIdAndStatusInOrderByCreatedDateDesc(pageable, parentCommentId, statuses);

        if (pages.isEmpty()
            || pages.get().findFirst().filter(c -> c.getEvent().getId().equals(eventId)).isEmpty()) {
            throw new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + parentCommentId);
        }

        UserVO user = userVO == null ? UserVO.builder().build() : userVO;
        List<EventCommentDto> eventCommentDtos = convertToEventCommentDtos(pages, user);

        return new PageableDto<>(
            eventCommentDtos,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages());
    }

    private List<EventCommentDto> convertToEventCommentDtos(Page<EventComment> pages, UserVO user) {
        return pages.stream()
            .map(comment -> setCurrentUserLikedToComment(comment, user))
            .map(comment -> modelMapper.map(comment, EventCommentDto.class))
            .toList();
    }

    private EventComment setCurrentUserLikedToComment(EventComment comment, UserVO user) {
        comment.setCurrentUserLiked(comment.getUsersLiked().stream()
            .anyMatch(u -> u.getId().equals(user.getId())));
        return comment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countAllActiveReplies(Long eventId, Long parentCommentId) {
        Optional<EventComment> comment = eventCommentRepo.findByIdAndStatusNot(parentCommentId, CommentStatus.DELETED)
            .filter(c -> c.getEvent().getId().equals(eventId));
        if (comment.isEmpty()) {
            throw new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + parentCommentId);
        }
        return eventCommentRepo.countByParentCommentIdAndStatusNot(parentCommentId, CommentStatus.DELETED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void like(Long eventId, Long commentId, UserVO userVO) {
        EventComment comment = eventCommentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)
            .filter(c -> c.getEvent().getId().equals(eventId))
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
            notificationService.sendEmailNotificationLikes(GeneralEmailMessage.builder()
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
     * {@inheritDoc}
     */
    @Override
    public AmountCommentLikesDto countLikes(Long eventId, Long commentId, UserVO userVO) {
        EventComment comment = eventCommentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)
            .filter(c -> c.getEvent().getId().equals(eventId))
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + commentId));

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
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void eventCommentLikeAndCount(AmountCommentLikesDto amountCommentLikesDto) {
        EventComment comment = eventCommentRepo.findById(amountCommentLikesDto.getId())
            .orElseThrow(() -> new BadRequestException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
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
     * @param eventVO   {@link EventVO} event id.
     * @param userVO    {@link UserVO} comment author.
     * @param commentVO {@link EventCommentVO} comment.
     */
    private void sendNotificationToTaggedUser(EventVO eventVO, UserVO userVO, EventCommentVO commentVO, Locale locale) {
        String commentText = commentVO.getText();
        Set<Long> usersId = getUserIdFromComment(commentText);
        if (!usersId.isEmpty()) {
            for (Long userId : usersId) {
                User user = userRepo.findById(userId)
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));
                UserTaggedInCommentMessage message = UserTaggedInCommentMessage.builder()
                    .commentedElementId(eventVO.getId())
                    .elementName(eventVO.getTitle())
                    .taggerName(userVO.getName())
                    .language(locale.getLanguage())
                    .creationDate(commentVO.getCreatedDate())
                    .receiverName(user.getName())
                    .receiverEmail(user.getEmail())
                    .commentText(commentText)
                    .baseLink(getBaseLink(eventVO.getId()))
                    .build();
                notificationService.sendUsersTaggedInCommentEmailNotification(message);
                userNotificationService.createNotification(modelMapper.map(user, UserVO.class), commentVO.getUser(),
                    NotificationType.EVENT_COMMENT_USER_TAG, commentVO.getId(),
                    commentText, eventVO.getId(), eventVO.getTitle());
            }
        }
    }

    /**
     * Method to extract user id from comment.
     *
     * @param message comment from {@link AddEventCommentDtoResponse}.
     * @return user id if present or null.
     */
    public Set<Long> getUserIdFromComment(String message) {
        String regEx = "data-userid=\"(\\d+)\"";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(message);
        Set<Long> userIds = new HashSet<>();
        if (!matcher.find()) {
            return userIds;
        }
        matcher.reset();
        while (matcher.find()) {
            userIds.add(Long.valueOf(matcher.group(1)));
        }
        return userIds;
    }

    private String getBaseLink(Long id) {
        return clientAddress + "/#/events/" + id;
    }
}
