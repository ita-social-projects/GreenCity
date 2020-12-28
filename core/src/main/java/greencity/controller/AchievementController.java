package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.achievement.AchievementDTO;
import greencity.dto.achievement.AchievementNotification;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.user.UserVO;
import greencity.service.AchievementService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/achievements")
public class AchievementController {
    private final AchievementService achievementService;

    /**
     * Constructor with parameters.
     */
    @Autowired
    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    /**
     * Method returns all achievements, available for achieving.
     *
     * @return list of {@link AchievementDTO}
     */
    @ApiOperation(value = "Get all achievements.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("")
    public ResponseEntity<List<AchievementVO>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(achievementService.findAll());
    }

    /**
     * Method notifies of the achievement.
     *
     * @param userId of {@link UserVO}
     * @return list {@link AchievementNotification}
     */
    @MessageMapping("")
    @SendTo("/topic/notification")
    public List<AchievementNotification> getNotification(Long userId) throws InterruptedException {
        Thread.sleep(60000);
        return achievementService.findAchievementsWithStatusActive(userId);
    }
}
