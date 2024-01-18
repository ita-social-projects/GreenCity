package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.ActionDto;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.event.EventVO;
import greencity.dto.filter.FilterNotificationDto;
import greencity.dto.notification.NotificationDto;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventComment;
import greencity.enums.NotificationType;
import greencity.enums.ProjectName;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EcoNewsCommentRepo;
import greencity.repository.EcoNewsRepo;
import greencity.repository.EventCommentRepo;
import greencity.repository.NotificationRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of {@link UserNotificationService}.
 */
@Service
@AllArgsConstructor
@Transactional
public class UserNotificationServiceImpl implements UserNotificationService {
    private final NotificationRepo notificationRepo;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private SimpMessagingTemplate messagingTemplate;
    private final EcoNewsRepo ecoNewsRepo;
    private EcoNewsCommentRepo ecoNewsCommentRepo;
    private EventCommentRepo eventCommentRepo;

    /**
     * {@inheritDoc}
     */
    public List<NotificationDto> getThreeLastNotifications(Principal principal) {
        User currentUser = modelMapper.map(userService.findByEmail(principal.getName()), User.class);
        Long userId = currentUser.getId();
        List<Notification> notifications = notificationRepo.findTop3ByTargetUserIdAndViewedFalseOrderByTimeDesc(userId);
        return notifications.stream()
                .map(notification -> modelMapper.map(notification, NotificationDto.class))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<NotificationDto> getNotificationsFiltered(Pageable page, Principal principal,
                                                                         FilterNotificationDto filter) {
        User currentUser = modelMapper.map(userService.findByEmail(principal.getName()), User.class);
        Long userId = currentUser.getId();
        if (filter.getProjectName().length == 0) {
            filter.setProjectName(ProjectName.values());
        }
        if (filter.getNotificationType().length == 0) {
            filter.setNotificationType(NotificationType.values());
        }
        Page<Notification> notifications =
                notificationRepo.findByTargetUserIdAndProjectNameInAndNotificationTypeInOrderByTimeDesc(userId,
                        filter.getProjectName(),
                        filter.getNotificationType(), page);
        return buildPageableAdvancedDto(notifications);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<NotificationDto> getNotifications(Pageable pageable, Principal principal) {
        User currentUser = modelMapper.map(userService.findByEmail(principal.getName()), User.class);
        Long userId = currentUser.getId();
        Page<Notification> notifications = notificationRepo.findByTargetUserId(userId, pageable);
        return buildPageableAdvancedDto(notifications);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationDto getNotification(Principal principal, Long notificationId) {
        Long userId = modelMapper.map(userService.findByEmail(principal.getName()), User.class).getId();
        Notification notification = notificationRepo.findByIdAndTargetUserId(notificationId, userId);
        if (notification == null) {
            throw new NotFoundException("Notification not found with id: " + notificationId);
        }
        notificationRepo.markNotificationAsViewed(notificationId);
        return modelMapper.map(notification, NotificationDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notificationSocket(ActionDto user) {
        Optional<Notification> userNotification =
                notificationRepo.findNotificationByTargetUserIdAndViewedIsFalse(user.getUserId());
        System.out.println(userNotification);
        messagingTemplate
                .convertAndSend("/topic/" + user.getUserId() + "/notification", userNotification.isPresent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createEcoNewsCommentNotification(UserVO targetUserVO, UserVO actionUserVO, Long ecoNewsCommentId,
                                                 NotificationType notificationType) {
        EcoNewsComment comment = ecoNewsCommentRepo.findById(ecoNewsCommentId)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        List<Notification> notifications = notificationRepo.findByTargetUserIdAndNotificationTypeAndViewedIsFalse
                (targetUserVO.getId(), notificationType);
        Notification notification = notifications.stream()
                .filter(n -> n.getEcoNewsComment().equals(comment))
                .findFirst()
                .orElse(Notification.builder()
                        .notificationType(notificationType)
                        .projectName(ProjectName.GREENCITY)
                        .targetUser(modelMapper.map(targetUserVO, User.class))
                        .actionUsers(new ArrayList<>())
                        .ecoNewsComment(comment)
                        .customMessage(comment.getText())
                        .build());
        notification.getActionUsers().add(modelMapper.map(actionUserVO, User.class));
        notification.setTime(LocalDateTime.now());
        notificationRepo.save(notification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createEcoNewsNotification(UserVO actionUserVO, Long ecoNewsId, NotificationType notificationType) {
        EcoNews ecoNews = ecoNewsRepo.findById(ecoNewsId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ECO_NEWS_NOT_FOUND_BY_ID + ecoNewsId));
        List<Notification> notifications = notificationRepo.findByTargetUserIdAndNotificationTypeAndViewedIsFalse
                (ecoNews.getAuthor().getId(), notificationType);
        Notification notification = notifications.stream()
                .filter(n -> n.getEcoNews().equals(ecoNews))
                .findFirst()
                .orElse(Notification.builder()
                        .notificationType(notificationType)
                        .projectName(ProjectName.GREENCITY)
                        .targetUser(ecoNews.getAuthor())
                        .actionUsers(new ArrayList<>())
                        .ecoNews(ecoNews)
                        .customMessage(ecoNews.getTitle())
                        .build());
        notification.getActionUsers().add(modelMapper.map(actionUserVO, User.class));
        notification.setTime(LocalDateTime.now());
        notificationRepo.save(notification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createEcoNewsCreatedNotification(UserVO targetUserVO, EcoNewsVO ecoNewsVO) {
        EcoNews ecoNews = modelMapper.map(ecoNewsVO, EcoNews.class);
        List<Notification> notifications = notificationRepo
                .findByTargetUserIdAndNotificationTypeAndViewedIsFalse(targetUserVO.getId(),
                        NotificationType.ECONEWS_CREATED);
        Notification notification = notifications.stream()
                .filter(n -> n.getEcoNews().equals(ecoNews))
                .findFirst()
                .orElse(Notification.builder()
                        .notificationType(NotificationType.ECONEWS_CREATED)
                        .projectName(ProjectName.GREENCITY)
                        .targetUser(modelMapper.map(targetUserVO, User.class))
                        .ecoNews(ecoNews)
                        .customMessage(ecoNews.getTitle())
                        .build());
        notification.setTime(LocalDateTime.now());
        notificationRepo.save(notification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createEventCreatedNotification(UserVO targetUserVO, EventVO eventVO) {
        Event event = modelMapper.map(eventVO, Event.class);
        List<Notification> notifications = notificationRepo
                .findByTargetUserIdAndNotificationTypeAndViewedIsFalse(targetUserVO.getId(),
                        NotificationType.EVENT_CREATED);
        Notification notification = notifications.stream()
                .filter(n -> n.getEvent().equals(event))
                .findFirst()
                .orElse(Notification.builder()
                        .notificationType(NotificationType.EVENT_CREATED)
                        .projectName(ProjectName.GREENCITY)
                        .targetUser(modelMapper.map(targetUserVO, User.class))
                        .event(event)
                        .actionUsers(new ArrayList<>())
                        .customMessage(event.getTitle())
                        .build());
        notification.setTime(LocalDateTime.now());
        notificationRepo.save(notification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createEventCommentNotification(UserVO targetUserVO, UserVO actionUserVO, Long commentId,
                                               NotificationType notificationType) {
        EventComment comment = eventCommentRepo.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + commentId));
        List<Notification> notifications = notificationRepo.findByTargetUserIdAndNotificationTypeAndViewedIsFalse
                (targetUserVO.getId(), notificationType);
        Notification notification = notifications.stream()
                .filter(n -> n.getEventComment().equals(comment))
                .findFirst()
                .orElse(Notification.builder()
                        .notificationType(notificationType)
                        .projectName(ProjectName.GREENCITY)
                        .targetUser(modelMapper.map(targetUserVO, User.class))
                        .eventComment(comment)
                        .actionUsers(new ArrayList<>())
                        .customMessage(comment.getText())
                        .build());
        notification.getActionUsers().add(modelMapper.map(actionUserVO, User.class));
        notification.setTime(LocalDateTime.now());
        notificationRepo.save(notification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createEventNotification(UserVO targetUserVO, UserVO actionUserVO, EventVO eventVO,
                                        NotificationType notificationType) {
        List<Notification> notifications = notificationRepo.findByTargetUserIdAndNotificationTypeAndViewedIsFalse
                (targetUserVO.getId(), notificationType);
        Event event = modelMapper.map(eventVO, Event.class);
        Notification foundNotification = notifications.stream()
                .filter(notification -> notification.getEvent().equals(event))
                .findFirst()
                .orElse(Notification.builder()
                        .notificationType(notificationType)
                        .projectName(ProjectName.GREENCITY)
                        .targetUser(modelMapper.map(targetUserVO, User.class))
                        .actionUsers(new ArrayList<>())
                        .event(event)
                        .customMessage(eventVO.getTitle())
                        .build());
        foundNotification.getActionUsers().add(modelMapper.map(actionUserVO, User.class));
        foundNotification.setTime(LocalDateTime.now());
        notificationRepo.save(foundNotification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createEventNotificationForAttenders(List<UserVO> attendersList, String title,
                                                    NotificationType notificationType, EventVO eventVO) {
        for (UserVO targetUserVO: attendersList) {
            Notification notification = Notification.builder()
                    .notificationType(notificationType)
                    .projectName(ProjectName.GREENCITY)
                    .targetUser(modelMapper.map(targetUserVO, User.class))
                    .time(LocalDateTime.now())
                    .event(modelMapper.map(eventVO, Event.class))
                    .customMessage(title)
                    .build();
            notificationRepo.save(notification);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createNotification(UserVO targetUser, UserVO actionUser, NotificationType notificationType) {
        Notification notification = Notification.builder()
                .notificationType(notificationType)
                .projectName(ProjectName.GREENCITY)
                .targetUser(modelMapper.map(targetUser, User.class))
                .time(LocalDateTime.now())
                .actionUsers(List.of(modelMapper.map(actionUser, User.class)))
                .build();
        notificationRepo.save(notification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEventCommentNotification(UserVO targetUserVO, UserVO actionUserVO, Long commentId,
                                          NotificationType notificationType) {
        Notification notification = notificationRepo.findByTargetUserIdAndNotificationTypeAndEventCommentId
                (targetUserVO.getId(), notificationType, commentId);
        removeActionUser(notification, modelMapper.map(actionUserVO, User.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEventCommentNotification(Long eventCommentId) {
        notificationRepo.deleteAllByEventCommentId(eventCommentId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEcoNewsCommentNotification(UserVO targetUserVO, UserVO actionUserVO, Long commentId,
                                               NotificationType notificationType) {
        Notification notification = notificationRepo.findByTargetUserIdAndNotificationTypeAndEcoNewsCommentId
                (targetUserVO.getId(), notificationType, commentId);
        removeActionUser(notification, modelMapper.map(actionUserVO, User.class));
    }

    private void removeActionUser(Notification notification, User actionUser) {
        if (notification != null) {
            if (notification.getActionUsers().size() == 1) {
                notificationRepo.delete(notification);
                return;
            }
            notification.getActionUsers().remove(actionUser);
            notificationRepo.save(notification);
        }
    }

    private PageableAdvancedDto<NotificationDto> buildPageableAdvancedDto(Page<Notification> notifications) {
        List<NotificationDto> notificationDtoList = modelMapper.map(notifications.getContent(),
                new TypeToken<List<NotificationDto>>() {
                }.getType());
        return new PageableAdvancedDto<>(
                notificationDtoList,
                notifications.getTotalElements(),
                notifications.getPageable().getPageNumber(),
                notifications.getTotalPages(),
                notifications.getNumber(),
                notifications.hasPrevious(),
                notifications.hasNext(),
                notifications.isFirst(),
                notifications.isLast());
    }
}
