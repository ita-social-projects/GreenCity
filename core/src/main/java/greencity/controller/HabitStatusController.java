package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.habitstatus.HabitStatusDto;
import greencity.dto.habitstatus.UpdateHabitStatusDto;
import greencity.service.HabitStatusService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.time.LocalDate;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/habit/status")
public class HabitStatusController {
    private final HabitStatusService habitStatusService;

    /**
     * Method return {@link greencity.entity.HabitStatus} for user by habit.
     *
     * @param habitAssignId - id of habitAssign
     * @return {@link HabitStatusDto}
     */
    @ApiOperation(value = "Get habit status for user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/{habitAssignId}")
    public ResponseEntity<HabitStatusDto> getHabitStatusForUser(@PathVariable Long habitAssignId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitStatusService.findStatusByHabitAssignId(habitAssignId));
    }

    /**
     * Method enroll {@link greencity.entity.Habit}.
     *
     * @param habitAssignId - id of habitAssign which we enroll
     * @return {@link HabitStatusDto}
     */
    @ApiOperation(value = "Enroll habit.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/enroll/{habitAssignId}")
    public ResponseEntity<HabitStatusDto> enrollHabit(@PathVariable Long habitAssignId) {
        return ResponseEntity.status(HttpStatus.OK).body(habitStatusService.enrollHabit(habitAssignId));
    }

    /**
     * Method unenroll Habit in defined date.
     *
     * @param habitAssignId - id of habitAssign
     * @param date          - date we want unenroll
     * @return {@link ResponseEntity}
     */
    @ApiOperation(value = "Unenroll habit.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/unenroll/{habitAssignId}/{date}")
    public ResponseEntity<HabitStatusDto> unenrollHabit(@PathVariable Long habitAssignId,
                                                        @PathVariable(value = "date")
                                                        @DateTimeFormat(pattern = "MM-dd-yyyy") LocalDate date) {
        habitStatusService.unenrollHabit(date, habitAssignId);
        return ResponseEntity.ok().build();
    }

    /**
     * Method enroll habit for defined date.
     *
     * @param habitAssignId - id of habit
     * @param date          - date we want enroll
     * @return {@link HabitStatusDto}
     */
    @ApiOperation(value = "Enroll for a specific day.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/enroll/{habitAssignId}/{date}")
    public ResponseEntity<HabitStatusDto> enrollHabitInDate(@PathVariable Long habitAssignId,
                                                            @PathVariable(value = "date")
                                                            @DateTimeFormat(pattern = "MM-dd-yyyy") LocalDate date) {
        habitStatusService.enrollHabitInDate(habitAssignId, date);
        return ResponseEntity.ok().build();
    }

    /**
     * Method to delete {@link greencity.entity.HabitStatus} for {@link greencity.entity.HabitAssign} by it's id.
     *
     * @param habitAssignId - id of {@link greencity.entity.HabitAssign}
     */
    @ApiOperation(value = "Delete status for habit assign.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @DeleteMapping("/{habitAssignId}")
    public ResponseEntity<HabitStatusDto> deleteHabitStatusByHabitAssign(@PathVariable Long habitAssignId) {
        habitStatusService.deleteStatusByHabitAssignId(habitAssignId);
        return ResponseEntity.ok().build();
    }

    /**
     * Method to update {@link greencity.entity.HabitStatus} for {@link greencity.entity.HabitAssign} by it's id.
     *
     * @param habitAssignId - id of {@link greencity.entity.HabitAssign}
     * @return {@link HabitStatusDto}
     */
    @ApiOperation(value = "Update status for habit assign.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping("/{habitAssignId}")
    public ResponseEntity<HabitStatusDto> update(
        @PathVariable Long habitAssignId, @Valid @RequestBody UpdateHabitStatusDto habitStatusForUpdateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(habitStatusService
            .update(habitAssignId, habitStatusForUpdateDto));
    }
}