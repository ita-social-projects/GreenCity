package greencity.controller;

import greencity.annotations.ApiPageableWithoutSort;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.ActionDto;
import greencity.dto.filter.FilterNotificationDto;
import greencity.dto.notification.NotificationDto;
import greencity.service.UserNotificationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/notification")
public class NotificationController {
    private UserNotificationService userNotificationService;

    /**
     * Method returns 3 last new notifications.
     *
     * @return set of 3 {@link NotificationDto}
     */
    @ApiOperation(value = "Get 3 last new notifications.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/new")
    public ResponseEntity<List<NotificationDto>> getThreeLastNotifications(
        @ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(userNotificationService.getThreeLastNotifications(principal));
    }

    /**
     * Method for getting page of notifications.
     *
     * @return list of {@link NotificationDto}
     */
    @ApiOperation(value = "Get page of notifications")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @ApiPageableWithoutSort
    @GetMapping("/all")
    public ResponseEntity<PageableAdvancedDto<NotificationDto>> getEvent(
        @ApiIgnore Pageable pageable,
        @ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(userNotificationService.getNotifications(pageable, principal));
    }

    /**
     * Method for getting page of notifications filtered and sorted.
     *
     * @return list of {@link NotificationDto}
     */
    @ApiOperation(value = "Get page of notification filtered and sorted.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @ApiPageableWithoutSort
    @GetMapping("/filtered")
    public ResponseEntity<PageableAdvancedDto<NotificationDto>> getEventFiltered(
        @ApiIgnore Pageable pageable,
        @ApiIgnore Principal principal,
        FilterNotificationDto filter) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(userNotificationService.getNotificationsFiltered(pageable, principal, filter));
    }

    /**
     * Method for returning specific Notification.
     *
     * @return list of {@link NotificationDto}
     */
    @ApiOperation(value = "Get single Notification.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping("/view/{notificationId}")
    public ResponseEntity<NotificationDto> getNotification(
        @ApiIgnore Principal principal,
        @PathVariable Long notificationId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(userNotificationService.getNotification(principal, notificationId));
    }

    /**
     * Socket to get new Notifications.
     */
    @MessageMapping("/notifications")
    public void notificationSocket(@Payload ActionDto user) {
        userNotificationService.notificationSocket(user);
    }
}
