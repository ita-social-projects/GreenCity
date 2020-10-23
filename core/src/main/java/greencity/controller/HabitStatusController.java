package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.habitstatus.HabitStatusDto;
import greencity.dto.habitstatus.UpdateHabitStatusDto;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatus;
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
     * Method return {@link HabitStatus} for user by {@link HabitAssign} id.
     *
     * @param habitAssignId - id of {@link HabitAssign}.
     * @return {@link HabitStatusDto}.
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
     * Method to enroll {@link HabitStatus}.
     *
     * @param habitAssignId -  id of {@link HabitAssign}.
     * @return {@link HabitStatusDto}.
     */
    @ApiOperation(value = "Enroll habit assign.")
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
     * Method to unenroll {@link HabitStatus} in defined date.
     *
     * @param habitAssignId - id of {@link HabitAssign}.
     * @param date          - {@link LocalDate} we want to unenroll.
     * @return {@link ResponseEntity}.
     */
    @ApiOperation(value = "Unenroll habit assign.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/unenroll/{habitAssignId}/{date}")
    public ResponseEntity<Object> unenrollHabit(@PathVariable Long habitAssignId,
                                                @PathVariable(value = "date")
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        habitStatusService.unenrollHabit(date, habitAssignId);
        return ResponseEntity.ok().build();
    }

    /**
     * Method to enroll habit for defined date.
     *
     * @param habitAssignId - id of {@link HabitAssign}.
     * @param date          - {@link LocalDate} we want to enroll.
     * @return {@link HabitStatusDto}.
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
                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                LocalDate date) {
        return ResponseEntity.status(HttpStatus.OK).body(habitStatusService.enrollHabitInDate(habitAssignId, date));
    }

    /**
     * Method to update {@link HabitStatus} for {@link HabitAssign} by it's id.
     *
     * @param habitAssignId - id of {@link HabitAssign}.
     * @return {@link HabitStatusDto}.
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
