package greencity.controller;

import greencity.annotations.CurrentUserId;
import greencity.constant.HttpStatuses;
import greencity.dto.goal.BulkCustomGoalDto;
import greencity.dto.goal.BulkSaveCustomGoalDto;
import greencity.dto.goal.CustomGoalResponseDto;
import greencity.dto.goal.CustomGoalVO;
import greencity.dto.user.BulkSaveUserGoalDto;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserVO;
import greencity.service.CustomGoalService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/custom/goals")
public class CustomGoalController {
    private final CustomGoalService customGoalService;

    /**
     * Method for finding all custom goals.
     *
     * @param userId user id.
     * @return list of {@link CustomGoalVO}
     */
    @ApiOperation(value = "Get all available custom goals")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/{userId}")
    public ResponseEntity<List<CustomGoalResponseDto>> getAllAvailableCustomGoals(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(customGoalService.findAllAvailableCustomGoals(userId));
    }

    /**
     * Method returns list user custom goals.
     *
     * @param userId {@link UserVO} id
     * @return list of {@link ResponseEntity}
     * @author Bogdan Kuzenko
     */
    @ApiOperation(value = "Get all user custom goals.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/{userId}/customGoals")
    public ResponseEntity<List<CustomGoalResponseDto>> findAllByUser(@PathVariable @CurrentUserId Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(customGoalService.findAllByUser(userId));
    }

    /**
     * Method saves custom goals for user.
     *
     * @param dto    {@link BulkSaveUserGoalDto} with list objects to save
     * @param userId {@link UserVO} id
     * @return new {@link ResponseEntity}
     * @author Bogdan Kuzenko
     */
    @ApiOperation(value = "Save one or multiple custom goals for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/{userId}/customGoals")
    public ResponseEntity<List<CustomGoalResponseDto>> saveUserCustomGoals(
        @Valid @RequestBody BulkSaveCustomGoalDto dto,
        @ApiParam("Id of current user. Cannot be empty.") @PathVariable @CurrentUserId Long userId) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(customGoalService.save(dto, userId));
    }

    /**
     * Method updated user custom goals.
     *
     * @param userId {@link UserVO} id
     * @param dto    {@link BulkCustomGoalDto} with list objects for update
     * @return new {@link ResponseEntity}
     * @author Bogdan Kuzenko
     */
    @ApiOperation(value = "Update user custom goals")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = UserRoleDto.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping("/{userId}/customGoals")
    public ResponseEntity<List<CustomGoalResponseDto>> updateBulk(@PathVariable @CurrentUserId Long userId,
        @Valid @RequestBody BulkCustomGoalDto dto) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(customGoalService.updateBulk(dto));
    }

    /**
     * Method for delete user custom goals.
     *
     * @param ids    string with objects id for deleting.
     * @param userId {@link UserVO} id
     * @return new {@link ResponseEntity}
     */
    @ApiOperation(value = "Delete user custom goals")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = Long.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @DeleteMapping("/{userId}/customGoals")
    public ResponseEntity<List<Long>> bulkDeleteCustomGoals(
        @ApiParam(value = "Ids of custom goals separated by a comma \n e.g. 1,2",
            required = true) @RequestParam String ids,
        @PathVariable @CurrentUserId Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(customGoalService.bulkDelete(ids));
    }
}
