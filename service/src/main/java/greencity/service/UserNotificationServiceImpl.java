package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.event.EventDto;
import greencity.dto.notification.FilterNotificationDto;
import greencity.dto.notification.NotificationDto;
import greencity.entity.Notification;
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
import java.util.Objects;
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
//        User currentUser = modelMapper.map(userService.findByEmail(principal.getName()), User.class);
//        Long userId = currentUser.getId();
        Long userId = 1L;
        List<Notification> notifications = notificationRepo.findTop3ByTargetUserIdAndViewedFalseOrderByTimeDesc(userId);
        return notifications.stream()
                .map(notification -> modelMapper.map(notification, NotificationDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public PageableAdvancedDto<NotificationDto> getNotifications(Pageable page, Principal principal,
                                                                 FilterNotificationDto filter) {
//        User currentUser = modelMapper.map(userService.findByEmail(principal.getName()), User.class);
//        Long userId = currentUser.getId();
        System.out.println(filter);
        Long userId = 1L;
        System.out.println(Objects.isNull(filter));
        Page<Notification> notifications =
                notificationRepo.findByTargetUserId(userId, page);
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
//
//    private List<Notification> filterNotifications(List<Notification> notifications, long userId, FilterNotificationDto filter) {
//        return null;
//    }
//
//    private PageableAdvancedDto<NotificationDto> buildPageableAdvancedDto(Page<Notification> notificationPage) {
//        List<NotificationDto> notificationDtos = modelMapper.map(notificationPage.getContent(), new TypeToken<List<NotificationDto>>() {
//        }.getType());
//        return new PageableAdvancedDto<>(
//                notificationDtos,
//                notificationPage.getTotalElements(),
//                notificationPage.getPageable().getPageNumber(),
//                notificationPage.getTotalPages(),
//                notificationPage.getNumber(),
//                notificationPage.hasPrevious(),
//                notificationPage.hasNext(),
//                notificationPage.isFirst(),
//                notificationPage.isLast());
//    }
//
//    private List<Notification> getNotificationsForCurrentPage(Pageable page, List<Notification> allNotifications) {
//        int startIndex = page.getPageNumber() * page.getPageSize();
//        int endIndex = Math.min(startIndex + page.getPageSize(), allNotifications.size());
//        return allNotifications.subList(startIndex, endIndex);
//    }
}
