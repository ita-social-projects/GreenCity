package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.ActionDto;
import greencity.dto.notification.EmailNotificationDto;
import greencity.dto.notification.NotificationDto;
import greencity.dto.user.UserVO;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.enums.NotificationType;
import greencity.enums.ProjectName;
import greencity.repository.NotificationRepo;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link UserNotificationService}.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserNotificationServiceImpl implements UserNotificationService {
    private final NotificationRepo notificationRepo;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private static final String TOPIC = "/topic/";
    private static final String NOTIFICATION = "/notification";

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<NotificationDto> getNotificationsFiltered(Pageable page, Principal principal,
        String language, ProjectName projectName, List<NotificationType> notificationTypes, Boolean viewed) {
        Long userId = userService.findByEmail(principal.getName()).getId();
        Page<Notification> notifications =
            notificationRepo.findNotificationsByFilter(userId, projectName, notificationTypes, viewed, page);
        return buildPageableAdvancedDto(notifications, language);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notificationSocket(ActionDto user) {
        boolean isExist = notificationRepo.existsByTargetUserIdAndViewedIsFalse(user.getUserId());
        messagingTemplate.convertAndSend(TOPIC + user.getUserId() + NOTIFICATION, isExist);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createNotificationForAttenders(List<UserVO> attendersList, String message,
        NotificationType notificationType, Long targetId) {
        for (UserVO targetUserVO : attendersList) {
            Notification notification = Notification.builder()
                .notificationType(notificationType)
                .projectName(ProjectName.GREENCITY)
                .targetUser(modelMapper.map(targetUserVO, User.class))
                .time(LocalDateTime.now())
                .targetId(targetId)
                .customMessage(message)
                .emailSent(false)
                .build();
            notificationService.sendEmailNotification(
                modelMapper.map(notificationRepo.save(notification), EmailNotificationDto.class));
            sendNotification(notification.getTargetUser().getId());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createNotificationForAttenders(List<UserVO> attendersList, String message,
        NotificationType notificationType, Long targetId, String title) {
        for (UserVO targetUserVO : attendersList) {
            Notification notification = Notification.builder()
                .notificationType(notificationType)
                .projectName(ProjectName.GREENCITY)
                .targetUser(modelMapper.map(targetUserVO, User.class))
                .time(LocalDateTime.now())
                .targetId(targetId)
                .customMessage(message)
                .secondMessage(title)
                .emailSent(false)
                .build();
            notificationService.sendEmailNotification(
                modelMapper.map(notificationRepo.save(notification), EmailNotificationDto.class));
            sendNotification(notification.getTargetUser().getId());
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
            .emailSent(false)
            .build();
        notificationService.sendEmailNotification(
            modelMapper.map(notificationRepo.save(notification), EmailNotificationDto.class));
        sendNotification(notification.getTargetUser().getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createNotification(UserVO targetUserVO, UserVO actionUserVO, NotificationType notificationType,
        Long targetId, String customMessage) {
        Notification notification = notificationRepo
            .findNotificationByTargetUserIdAndNotificationTypeAndTargetIdAndViewedIsFalse(targetUserVO.getId(),
                notificationType, targetId)
            .orElse(Notification.builder()
                .notificationType(notificationType)
                .projectName(ProjectName.GREENCITY)
                .targetUser(modelMapper.map(targetUserVO, User.class))
                .actionUsers(new ArrayList<>())
                .targetId(targetId)
                .customMessage(customMessage)
                .emailSent(false)
                .build());
        notification.getActionUsers().add(modelMapper.map(actionUserVO, User.class));
        notification.setTime(LocalDateTime.now());
        notificationService.sendEmailNotification(
            modelMapper.map(notificationRepo.save(notification), EmailNotificationDto.class));
        sendNotification(notification.getTargetUser().getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createNotification(UserVO targetUserVO, UserVO actionUserVO, NotificationType notificationType,
        Long targetId, String customMessage, Long secondMessageId,
        String secondMessageText) {
        Notification notification = notificationRepo
            .findNotificationByTargetUserIdAndNotificationTypeAndTargetIdAndViewedIsFalse(targetUserVO.getId(),
                notificationType, targetId)
            .orElse(Notification.builder()
                .notificationType(notificationType)
                .projectName(ProjectName.GREENCITY)
                .targetUser(modelMapper.map(targetUserVO, User.class))
                .actionUsers(new ArrayList<>())
                .targetId(targetId)
                .customMessage(customMessage)
                .secondMessageId(secondMessageId)
                .secondMessage(secondMessageText)
                .emailSent(false)
                .build());
        notification.getActionUsers().add(modelMapper.map(actionUserVO, User.class));
        notification.setTime(LocalDateTime.now());
        notification.setCustomMessage(customMessage);
        notificationService.sendEmailNotification(
            modelMapper.map(notificationRepo.save(notification), EmailNotificationDto.class));
        sendNotification(notification.getTargetUser().getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createNewNotification(UserVO targetUserVO, NotificationType notificationType, Long targetId,
        String customMessage) {
        Notification notification = Notification.builder()
            .notificationType(notificationType)
            .projectName(ProjectName.GREENCITY)
            .targetUser(modelMapper.map(targetUserVO, User.class))
            .targetId(targetId)
            .customMessage(customMessage)
            .time(LocalDateTime.now())
            .emailSent(false)
            .build();
        notificationService.sendEmailNotification(
            modelMapper.map(notificationRepo.save(notification), EmailNotificationDto.class));
        sendNotification(notification.getTargetUser().getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeActionUserFromNotification(UserVO targetUserVO, UserVO actionUserVO, Long targetId,
        NotificationType notificationType) {
        Notification notification = notificationRepo.findNotificationByTargetUserIdAndNotificationTypeAndTargetId(
            targetUserVO.getId(), notificationType, targetId);
        if (notification != null) {
            if (notification.getActionUsers().size() == 1) {
                notificationRepo.delete(notification);
                return;
            }
            User user = modelMapper.map(actionUserVO, User.class);
            notification.getActionUsers()
                .removeIf(u -> u.getId().equals(user.getId()));
            notificationRepo.save(notification);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteNotification(Principal principal, Long notificationId) {
        Long userId = userService.findByEmail(principal.getName()).getId();
        notificationRepo.deleteNotificationByIdAndTargetUserId(notificationId, userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unreadNotification(Long notificationId) {
        Long userId = notificationRepo.findById(notificationId)
            .orElseThrow().getTargetUser().getId();
        long count = notificationRepo.countByTargetUserIdAndViewedIsFalse(userId);
        if (count == 0) {
            messagingTemplate
                .convertAndSend(TOPIC + userId + NOTIFICATION, true);
        }
        notificationRepo.markNotificationAsNotViewed(notificationId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void viewNotification(Long notificationId) {
        Long userId = notificationRepo.findById(notificationId)
            .orElseThrow().getTargetUser().getId();
        long count = notificationRepo.countByTargetUserIdAndViewedIsFalse(userId);
        if (count == 1) {
            messagingTemplate
                .convertAndSend(TOPIC + userId + NOTIFICATION, false);
        }
        notificationRepo.markNotificationAsViewed(notificationId);
    }

    private PageableAdvancedDto<NotificationDto> buildPageableAdvancedDto(Page<Notification> notifications,
        String language) {
        List<NotificationDto> notificationDtoList = new LinkedList<>();
        for (Notification n : notifications) {
            notificationDtoList.add(createNotificationDto(n, language));
        }
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

    /**
     * Method used to create {@link NotificationDto} from {@link Notification},
     * adding localized notification text.
     *
     * @param notification that should be transformed into dto
     * @param language     language code
     * @return mapped and localized {@link NotificationDto}
     */
    private NotificationDto createNotificationDto(Notification notification, String language) {
        NotificationDto dto = modelMapper.map(notification, NotificationDto.class);
        ResourceBundle bundle = ResourceBundle.getBundle("notification", Locale.forLanguageTag(language),
            ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT));
        dto.setTitleText(bundle.getString(dto.getNotificationType() + "_TITLE"));
        dto.setBodyText(bundle.getString(dto.getNotificationType()));
        int size = new HashSet<>(notification.getActionUsers()).size();
        if (size == 1) {
            User actionUser = notification.getActionUsers().getFirst();
            dto.setActionUserId(actionUser.getId());
            dto.setActionUserText(actionUser.getName());
        } else {
            dto.setActionUserText(size + " " + bundle.getString("USERS"));
        }
        return dto;
    }

    /**
     * Sends a new notification to a specified user.
     *
     * @param userId the ID of the user to whom the notification will be sent
     */
    private void sendNotification(Long userId) {
        messagingTemplate.convertAndSend(TOPIC + userId + NOTIFICATION, true);
    }

    @Override
    public void createOrUpdateHabitInviteNotification(UserVO targetUserVO, UserVO actionUserVO, Long habitId,
        String habitName) {
        Optional<Notification> existingNotification = notificationRepo
            .findNotificationByTargetUserIdAndNotificationTypeAndTargetIdAndViewedIsFalse(targetUserVO.getId(),
                NotificationType.HABIT_INVITE, habitId);

        String customMessage;
        if (existingNotification.isPresent()) {
            Notification notification = existingNotification.get();
            notification.getActionUsers().add(modelMapper.map(actionUserVO, User.class));

            customMessage = createInvitationNotificationMessage(notification.getActionUsers(), habitName);

            notification.setCustomMessage(customMessage);
            notification.setTime(LocalDateTime.now());
            notificationRepo.save(notification);
        } else {
            customMessage = String.format("%s invites you to add new habit %s.",
                actionUserVO.getName(), habitName);

            createNotification(targetUserVO, actionUserVO, NotificationType.HABIT_INVITE, habitId, customMessage);
        }
    }

    private String createInvitationNotificationMessage(List<User> actionUsers, String habitName) {
        int userCount = actionUsers.size();

        return switch (userCount) {
            case 1 -> String.format("%s invites you to add new habit %s.",
                actionUsers.get(0).getName(), habitName);
            case 2 -> String.format("%s and %s invite you to add new habit %s.",
                actionUsers.get(0).getName(), actionUsers.get(1).getName(), habitName);
            default -> String.format("%s, %s and other users invite you to add new habit %s.",
                actionUsers.get(userCount - 2).getName(), actionUsers.get(userCount - 1).getName(), habitName);
        };
    }

    @Override
    public void createOrUpdateLikeNotification(UserVO targetUserVO, UserVO actionUserVO, Long newsId, String newsTitle,
        boolean isLike) {
        notificationRepo.findNotificationByTargetUserIdAndNotificationTypeAndTargetIdAndViewedIsFalse(
            targetUserVO.getId(), NotificationType.ECONEWS_LIKE, newsId)
            .ifPresentOrElse(notification -> {
                List<User> actionUsers = notification.getActionUsers();

                actionUsers.removeIf(user -> user.getId().equals(actionUserVO.getId()));
                if (isLike) {
                    actionUsers.add(modelMapper.map(actionUserVO, User.class));
                }

                if (actionUsers.isEmpty()) {
                    notificationRepo.delete(notification);
                } else {
                    notification.setCustomMessage(createLikeNotificationMessage(actionUsers, newsTitle));
                    notification.setTime(LocalDateTime.now());
                    notificationRepo.save(notification);
                }
            }, () -> {
                if (isLike) {
                    String customMessage = String.format("%s likes your news %s.",
                        actionUserVO.getName(), newsTitle);
                    createNotification(targetUserVO, actionUserVO, NotificationType.ECONEWS_LIKE, newsId,
                        customMessage);
                }
            });
    }

    private String createLikeNotificationMessage(List<User> actionUsers, String newsTitle) {
        int userCount = actionUsers.size();

        return switch (userCount) {
            case 1 -> String.format("%s likes your news %s.",
                actionUsers.get(0).getName(), newsTitle);
            case 2 -> String.format("%s and %s like your news %s.",
                actionUsers.get(0).getName(), actionUsers.get(1).getName(), newsTitle);
            default -> String.format("%s, %s and other users like your news %s.",
                actionUsers.get(userCount - 2).getName(), actionUsers.get(userCount - 1).getName(), newsTitle);
        };
    }
}
