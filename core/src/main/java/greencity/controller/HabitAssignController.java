package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitAssignStatDto;
import greencity.dto.user.UserVO;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import greencity.service.HabitAssignService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/habit")
public class HabitAssignController {
    private final HabitAssignService habitAssignService;

    /**
     * Method which assigns habit for {@link User}.
     *
     * @param id     {@link Habit} id.
     * @param userVO {@link UserVO} instance.
     * @return {@link ResponseEntity}.
     */
    @ApiOperation(value = "Assign habit for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/assign/{id}")
    public ResponseEntity<HabitAssignDto> assign(@PathVariable Long id,
        @ApiIgnore @CurrentUser UserVO userVO) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(habitAssignService.assignHabitForUser(id, userVO));
    }

    /**
     * Method returns {@link HabitAssign} by it's id.
     *
     * @param id {@link HabitAssign} id.
     * @return {@link HabitAssignDto}.
     */
    @ApiOperation(value = "Get habit assign.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/assign/{id}")
    public ResponseEntity<HabitAssignDto> getHabitAssign(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService.getById(id));
    }

    /**
     * Method to return all {@link HabitAssign} by it's {@link Habit} id.
     *
     * @param id       {@link Habit} id.
     * @param acquired {@link Boolean} status.
     * @return {@link List} of {@link HabitAssignDto}.
     */
    @ApiOperation(value = "Get all users assigns from certain habit.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = List.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{id}/assign/all")
    public ResponseEntity<List<HabitAssignDto>> getAllHabitAssignsByHabitIdAndAcquired(@PathVariable Long id,
        @RequestParam Boolean acquired) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService.getAllHabitAssignsByHabitIdAndAcquiredStatus(id, acquired));
    }

    /**
     * Method to return {@link HabitAssign} by it's {@link Habit} id.
     *
     * @param id     {@link Habit} id.
     * @param userVO {@link UserVO} user.
     * @return {@link HabitAssignDto} instance.
     */
    @ApiOperation(value = "Get active assign by habit id for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{id}/assign")
    public ResponseEntity<HabitAssignDto> getHabitAssignByHabitId(
        @ApiIgnore @CurrentUser UserVO userVO,
        @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService.findActiveHabitAssignByUserIdAndHabitId(userVO.getId(), id));
    }

    /**
     * Method to update active {@link HabitAssign} for it's {@link Habit} id and
     * current user.
     *
     * @param userVO             {@link UserVO} instance.
     * @param id                 {@link Habit} id.
     * @param habitAssignStatDto {@link HabitAssignStatDto} instance.
     * @return {@link HabitAssignDto}.
     */
    @ApiOperation(value = "Update active user habit assign acquired or suspended status.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PatchMapping("/{id}/assign")
    public ResponseEntity<HabitAssignDto> updateAssignByHabitId(
        @ApiIgnore @CurrentUser UserVO userVO,
        @PathVariable Long id, @Valid @RequestBody HabitAssignStatDto habitAssignStatDto) {
        return ResponseEntity.status(HttpStatus.OK).body(habitAssignService
            .updateStatusByHabitIdAndUserId(id, userVO.getId(), habitAssignStatDto));
    }
}
