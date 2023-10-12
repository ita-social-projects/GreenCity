package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.achievement.AchievementDTO;
import greencity.dto.achievement.AchievementVO;
import greencity.service.AchievementService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("")
    public ResponseEntity<List<AchievementVO>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(achievementService.findAll());
    }

    /**
     * Method returns all achievements, achieved by user.
     *
     * @return list of {@link AchievementDTO}
     */
    @ApiOperation(value = "Get all achievements by user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/achieved")
    public ResponseEntity<List<AchievementVO>> getAllAchievementsByUserID(@ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(achievementService.findAllByUserID(principal.getName()));
    }
}
