package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.filter.FilterNotificationDto;
import greencity.dto.notification.NotificationDto;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.enums.NotificationType;
import greencity.enums.ProjectName;
import greencity.repository.NotificationRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
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

    public List<NotificationDto> getThreeLastNotifications(Principal principal) {
        User currentUser = modelMapper.map(userService.findByEmail(principal.getName()), User.class);
        Long userId = currentUser.getId();
        List<Notification> notifications = notificationRepo.findTop3ByTargetUserIdAndViewedFalseOrderByTimeDesc(userId);
        return notifications.stream()
                .map(notification -> modelMapper.map(notification, NotificationDto.class))
                .collect(Collectors.toList());
    }

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

    @Override
    public PageableAdvancedDto<NotificationDto> getNotifications(Pageable pageable, Principal principal) {
        User currentUser = modelMapper.map(userService.findByEmail(principal.getName()), User.class);
        Long userId = currentUser.getId();
        Page<Notification> notifications = notificationRepo.findByTargetUserId(userId, pageable);
        return buildPageableAdvancedDto(notifications);
    }

    private PageableAdvancedDto<NotificationDto> buildPageableAdvancedDto(Page<Notification> notifications) {
        List<NotificationDto> notificationDtos = modelMapper.map(notifications.getContent(),
                new TypeToken<List<NotificationDto>>() {
                }.getType());
        return new PageableAdvancedDto<>(
                notificationDtos,
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
