package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.CurrentUser;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.constant.ValidationConstants;
import greencity.dto.goal.GoalRequestDto;
import greencity.dto.user.UserGoalResponseDto;
import greencity.dto.user.UserVO;
import greencity.service.GoalService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.Locale;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/goals")
public class GoalController {
    private final GoalService goalService;

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
    @PostMapping
    @ApiLocale
    public ResponseEntity<List<UserGoalResponseDto>> saveUserGoals(
        @Valid @RequestBody List<GoalRequestDto> dto,
        @ApiIgnore @CurrentUser UserVO user,
        Long habitId,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(goalService.saveUserGoals(user.getId(), habitId, dto, locale.getLanguage()));
    }

    /**
     * Method finds shoppingList saved by user in specific language.
     *
     * @param locale  {@link Locale} with needed language code.
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
    @GetMapping("/habits/{habitId}/shopping-list")
    @ApiLocale
    public ResponseEntity<List<UserGoalResponseDto>> getGoalsAssignedToUser(
        @ApiIgnore @CurrentUser UserVO user,
        @ApiParam("Id of the Habit that belongs to current user. Cannot be empty.") @PathVariable Long habitId,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(goalService.getUserGoals(user.getId(), habitId, locale.getLanguage()));
    }

    /**
     * Method deletes from shoppingList goal saved by user.
     *
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
    @DeleteMapping
    public void delete(
        @ApiIgnore @CurrentUser UserVO user, Long habitId, Long goalId) {
        goalService.deleteUserGoalByGoalIdAndUserIdAndHabitId(goalId, user.getId(), habitId);
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
    @PatchMapping("/{userGoalId}")
    @ApiLocale
    public ResponseEntity<UserGoalResponseDto> updateUserGoalStatus(
        @ApiIgnore @CurrentUser UserVO user,
        @ApiParam("Id of the UserGoal that belongs to current user. Cannot be empty.") @PathVariable Long userGoalId,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(goalService.updateUserGoalStatus(user.getId(), userGoalId, locale.getLanguage()));
    }

    /**
     * Method for deleting user goals.
     *
     * @param ids string with objects id for deleting.
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
    @DeleteMapping("/user-goals")
    public ResponseEntity<List<Long>> bulkDeleteUserGoals(
        @ApiParam(value = "Ids of user goals separated by a comma \n e.g. 1,2", required = true) @Pattern(
            regexp = "^\\d+(,\\d+)++$",
            message = ValidationConstants.BAD_COMMA_SEPARATED_NUMBERS) @RequestParam String ids,
        @ApiIgnore @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK).body(goalService
            .deleteUserGoals(ids));
    }
}
