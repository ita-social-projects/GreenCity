package greencity.controller;

import greencity.annotations.ApiPageableWithoutSort;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.ActionDto;
import greencity.dto.filter.FilterNotificationDto;
import greencity.dto.notification.NotificationDto;
import greencity.service.UserNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;
import java.util.List;
import java.util.Locale;

@RestController
@AllArgsConstructor
@RequestMapping("/notification")
public class NotificationController {
    private UserNotificationService userNotificationService;

    /**
     * Method returns 3 last not viewed notifications.
     *
     * @param principal Principal with userId
     * @param locale    language responseCode
     * @return set of 3 {@link NotificationDto}
     * @author Volodymyr Mladonov
     */
    @Operation(summary = "Get 3 last new notifications.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/new")
    public ResponseEntity<List<NotificationDto>> getThreeLastNotifications(
        @Parameter(hidden = true) Principal principal,
        @Parameter(hidden = true) @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(userNotificationService.getThreeLastNotifications(principal, locale.getLanguage()));
    }

    /**
     * Method for getting page of notifications filtered and sorted.
     *
     * @param pageable  pageable configuration
     * @param principal Principal with userId
     * @param filter    lists of tags, that should be returned for User
     * @param locale    language responseCode
     * @return list of {@link NotificationDto}
     * @author Volodymyr Mladonov
     */
    @Operation(summary = "Get page of notification filtered and sorted.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @ApiPageableWithoutSort
    @GetMapping("/all")
    public ResponseEntity<PageableAdvancedDto<NotificationDto>> getEventFiltered(
        @Parameter(hidden = true) Pageable pageable,
        @Parameter(hidden = true) Principal principal,
        FilterNotificationDto filter,
        @Parameter(hidden = true) @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(userNotificationService.getNotificationsFiltered(pageable, principal, filter, locale.getLanguage()));
    }

    /**
     * Method to mark Notification as viewed.
     *
     * @param notificationId id of notification, that should be returned
     * @return One {@link NotificationDto}
     * @author Volodymyr Mladonov
     */
    @Operation(summary = "Get single Notification.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PatchMapping("/view/{notificationId}")
    public ResponseEntity<NotificationDto> viewNotification(
        @PathVariable Long notificationId) {
        userNotificationService.viewNotification(notificationId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method to mark specific Notification as not viewed.
     *
     * @param notificationId id of notification, that should be marked as not viewed
     * @return One {@link NotificationDto}
     * @author Volodymyr Mladonov
     */
    @Operation(summary = "Unread single Notification.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PatchMapping("/unread/{notificationId}")
    public ResponseEntity<Object> unreadNotification(
        @PathVariable Long notificationId) {
        userNotificationService.unreadNotification(notificationId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method to delete specific Notification.
     *
     * @param principal      Principal with userId
     * @param notificationId id of notification, that should be deleted
     */
    @Operation(summary = "Delete single Notification.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Object> deleteNotification(
        @Parameter(hidden = true) Principal principal,
        @PathVariable Long notificationId) {
        userNotificationService.deleteNotification(principal, notificationId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Socket to get new Notifications.
     */
    @MessageMapping("/notifications")
    public void notificationSocket(@Payload ActionDto user) {
        userNotificationService.notificationSocket(user);
    }
}
