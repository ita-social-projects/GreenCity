package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.achievement.UserAchievementVO;
import greencity.service.AchievementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user-achievements")
@RequiredArgsConstructor
public class UserAchievementController {

    private final AchievementService achievementService;

    /**
     * Method returns all user achievements by user id
     *
     * @param userId id of the user
     *
     * @return list of {@link UserAchievementVO}
     */
    @Operation(summary = "Get all user achievements by user id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
                    content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<UserAchievementVO>> findAllByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok().body(achievementService.findAllByUserId(userId));
    }
}
