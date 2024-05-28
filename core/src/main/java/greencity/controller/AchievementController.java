package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.achievement.AchievementDTO;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.ActionDto;
import greencity.service.AchievementService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import java.security.Principal;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @Operation(summary = "Get all achievements by type.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
    })
    @GetMapping("")
    public ResponseEntity<List<AchievementVO>> getAll(@Parameter(hidden = true) Principal principal,
        @Parameter(description = "Available values : ACHIEVED, UNACHIEVED."
            + " Leave this field empty if you need items with any status") @RequestParam(
                required = false) String achievementStatus) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(achievementService.findAllByType(principal.getName(), achievementStatus));
    }

    /**
     * Method to test socket (delete).
     */
    @MessageMapping("/achieve")
    public void achieve(@Payload ActionDto user) {
        achievementService.achieve(user);
    }
}