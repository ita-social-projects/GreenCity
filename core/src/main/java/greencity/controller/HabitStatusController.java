package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.habitstatus.HabitStatusDto;
import greencity.service.HabitStatusService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.time.LocalDate;
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
}