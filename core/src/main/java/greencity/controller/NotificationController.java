package greencity.controller;

import greencity.annotations.ApiPageableWithoutSort;
import greencity.annotations.ValidLanguage;
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
     * @param locale    language code
     * @return set of 3 {@link NotificationDto}
     * @author Volodymyr Mladonov
     */
    @ApiOperation(value = "Get 3 last new notifications.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/new")
    public ResponseEntity<List<NotificationDto>> getThreeLastNotifications(
        @ApiIgnore Principal principal,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(userNotificationService.getThreeLastNotifications(principal, locale.getLanguage()));
    }

    /**
     * Method for getting page of notifications.
     *
     * @param pageable  pageable configuration
     * @param principal Principal with userId
     * @param locale    language code
     * @return list of {@link NotificationDto}
     * @author Volodymyr Mladonov
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
        @ApiIgnore Principal principal,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(userNotificationService.getNotifications(pageable, principal, locale.getLanguage()));
    }

    /**
     * Method for getting page of notifications filtered and sorted.
     *
     * @param pageable  pageable configuration
     * @param principal Principal with userId
     * @param filter    lists of tags, that should be returned for User
     * @param locale    language code
     * @return list of {@link NotificationDto}
     * @author Volodymyr Mladonov
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
        FilterNotificationDto filter,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(userNotificationService.getNotificationsFiltered(pageable, principal, filter, locale.getLanguage()));
    }

    /**
     * Method for returning specific Notification.
     *
     * @param principal      Principal with userId
     * @param notificationId id of notification, that should be returned
     * @param locale         language code
     * @return One {@link NotificationDto}
     * @author Volodymyr Mladonov
     */
    @ApiOperation(value = "Get single Notification.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/view/{notificationId}")
    public ResponseEntity<NotificationDto> getNotification(
        @ApiIgnore Principal principal,
        @PathVariable Long notificationId,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(userNotificationService.getNotification(principal, notificationId, locale.getLanguage()));
    }

    /**
     * Socket to get new Notifications.
     */
    @MessageMapping("/notifications")
    public void notificationSocket(@Payload ActionDto user) {
        userNotificationService.notificationSocket(user);
    }
}
