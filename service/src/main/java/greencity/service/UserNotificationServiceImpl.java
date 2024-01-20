package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.ActionDto;
import greencity.dto.filter.FilterNotificationDto;
import greencity.dto.notification.NotificationDto;
import greencity.dto.user.UserVO;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.enums.NotificationType;
import greencity.enums.ProjectName;
import greencity.exception.exceptions.NotFoundException;
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
    public void createNotificationForAttenders(List<UserVO> attendersList, String title,
                                                    NotificationType notificationType, Long targetId) {
        for (UserVO targetUserVO: attendersList) {
            Notification notification = Notification.builder()
                    .notificationType(notificationType)
                    .projectName(ProjectName.GREENCITY)
                    .targetUser(modelMapper.map(targetUserVO, User.class))
                    .time(LocalDateTime.now())
                    .targetId(targetId)
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
    public void createNotification(UserVO targetUserVO, UserVO actionUserVO, NotificationType notificationType,
                                   Long targetId, String customMessage) {
        Notification notification =
                notificationRepo.findNotificationByTargetUserIdAndNotificationTypeAndTargetIdAndViewedIsFalse
                                (targetUserVO.getId(), notificationType, targetId)
                        .orElse(Notification.builder()
                        .notificationType(notificationType)
                        .projectName(ProjectName.GREENCITY)
                        .targetUser(modelMapper.map(targetUserVO, User.class))
                        .actionUsers(new ArrayList<>())
                        .targetId(targetId)
                        .customMessage(customMessage)
                        .build());
        notification.getActionUsers().add(modelMapper.map(actionUserVO, User.class));
        notification.setTime(LocalDateTime.now());
        notificationRepo.save(notification);
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
                                .build();
        notificationRepo.save(notification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeActionUserFromNotification(UserVO targetUserVO, UserVO actionUserVO, Long targetId,
                                                 NotificationType notificationType) {
        Notification notification = notificationRepo.findNotificationByTargetUserIdAndNotificationTypeAndTargetId
                (targetUserVO.getId(), notificationType, targetId);
        if (notification != null) {
            if (notification.getActionUsers().size() == 1) {
                notificationRepo.delete(notification);
                return;
            }
            notification.getActionUsers().remove(modelMapper.map(actionUserVO, User.class));
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
