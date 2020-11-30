package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.CurrentUserId;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.constant.ValidationConstants;
import greencity.dto.goal.GoalDto;
import greencity.dto.goal.GoalRequestDto;
import greencity.dto.user.UserGoalResponseDto;
import greencity.dto.user.UserVO;
import greencity.service.GoalService;
import greencity.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.Locale;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class GoalController {
    private final GoalService goalService;
    private final UserService userService;

    /**
     * Method saves goals, chosen by user.
     *
     * @param dto    - dto with goals, chosen by user.
     * @param locale - needed language code
     * @return new {@link ResponseEntity}.
     * @author Vitalii Skolozdra
     */
    @ApiOperation(value = "Save one or multiple goals for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/{userId}/save-goals")
    @ApiLocale
    public ResponseEntity<List<UserGoalResponseDto>> saveUserGoals(
        @Valid @RequestBody List<GoalRequestDto> dto,
        @ApiParam("Id of current user. Cannot be empty.") @PathVariable @CurrentUserId Long userId,
        Long habitId,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(goalService.saveUserGoals(userId, habitId, dto, locale.getLanguage()));
    }

    /**
     * Method finds shoppingList saved by user in specific language.
     *
     * @param locale  {@link Locale} with needed language code.
     * @param userId  {@link Long} with needed user id.
     * @param habitId {@link Long} with needed habit id.
     * @return List of {@link UserGoalResponseDto}.
     * @author Dmytro Khonko
     */
    @ApiOperation(value = "Get user`s shopping list.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/{userId}/habits/{habitId}/shopping-list")
    @ApiLocale
    public ResponseEntity<List<UserGoalResponseDto>> getGoalsAssignedToUser(
        @ApiParam("Id of current user. Cannot be empty.") @PathVariable @CurrentUserId Long userId,
        @ApiParam("Id of the Habit that belongs to current user. Cannot be empty.") @PathVariable Long habitId,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(goalService.getUserGoals(userId, habitId, locale.getLanguage()));
    }

    /**
     * Method deletes from shoppingList goal saved by user.
     *
     * @param userId  {@link Long} with needed user id
     * @param goalId  {@link Long} with needed goal id.
     * @param habitId {@link Long} with needed habit id.
     * @author Dmytro Khonko
     */

    @ApiOperation(value = "Delete from shopping list")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @DeleteMapping("/{userId}/delete-goal/{habitId}")
    public void delete(
        @ApiParam("Cerrent user id") @PathVariable @CurrentUserId Long userId,
        @PathVariable Long habitId, Long goalId) {
        goalService.deleteUserGoalByGoalIdAndUserIdAndHabitId(goalId, userId, habitId);
    }

    /**
     * Method updates goal status.
     *
     * @param locale - needed language code
     * @return new {@link ResponseEntity}.
     * @author Vitalii Skolozdra
     */
    @ApiOperation(value = "Change status of one of the goals for current user to DONE.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PatchMapping("/{userId}/goals/{userGoalId}")
    @ApiLocale
    public ResponseEntity<UserGoalResponseDto> updateUserGoalStatus(
        @ApiParam("Id of current user. Cannot be empty.") @PathVariable @CurrentUserId Long userId,
        @ApiParam("Id of the UserGoal that belongs to current user. Cannot be empty.") @PathVariable Long userGoalId,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(goalService.updateUserGoalStatus(userId, userGoalId, locale.getLanguage()));
    }

    /**
     * Method for deleting user goals.
     *
     * @param ids    string with objects id for deleting.
     * @param userId {@link UserVO} id
     * @return new {@link ResponseEntity}
     * @author Bogdan Kuzenko
     */
    @ApiOperation(value = "Delete user goal")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = Long.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @DeleteMapping("/{userId}/userGoals")
    public ResponseEntity<List<Long>> bulkDeleteUserGoals(
        @ApiParam(value = "Ids of user goals separated by a comma \n e.g. 1,2", required = true) @Pattern(
            regexp = "^\\d+(,\\d+)*$",
            message = ValidationConstants.BAD_COMMA_SEPARATED_NUMBERS) @RequestParam String ids,
        @PathVariable @CurrentUserId Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(goalService
            .deleteUserGoals(ids));
    }
}
