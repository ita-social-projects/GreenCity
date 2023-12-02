package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.achievement.AchievementDTO;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.service.UserNotificationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
     * @return list of {@link AchievementDTO}
     */
    @ApiOperation(value = "Get all achievements by type.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HttpStatuses.OK),
            @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
            @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("")
    public ResponseEntity<List<NotificationDtoResponse>> getThreeLastNotifications(@ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userNotificationService.getThreeLastNotifications(principal.getName()));
    }
}
