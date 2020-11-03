package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.habitstatus.HabitStatusDto;
import greencity.dto.habitstatus.UpdateHabitStatusDto;
import greencity.dto.user.UserVO;
import greencity.entity.Habit;
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
import springfox.documentation.annotations.ApiIgnore;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/habit")
public class HabitStatusController {
    private final HabitStatusService habitStatusService;

    /**
     * Method return {@link HabitStatus} for user by {@link HabitAssign} id.
     *
     * @param id - id of {@link HabitAssign}.
     * @return {@link HabitStatusDto}.
     */
    @ApiOperation(value = "Get status of habit by assigned id for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitStatusDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/assign/{id}/status")
    public ResponseEntity<HabitStatusDto> getHabitStatusByHabitAssignId(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitStatusService.findStatusByHabitAssignId(id));
    }

    /**
     * Method return {@link HabitStatus} for user by {@link HabitAssign} id.
     *
     * @param id - id of {@link Habit}.
     * @return {@link HabitStatusDto}.
     */
    @ApiOperation(value = "Get active assigned status by habit id for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitStatusDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{id}/status")
    public ResponseEntity<HabitStatusDto> getHabitStatusByHabitId(
        @ApiIgnore @CurrentUser UserVO userVO,
        @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitStatusService.findActiveStatusByHabitIdAndUserId(id, userVO.getId()));
    }

    /**
     * Method to enroll {@link HabitStatus}.
     *
     * @param id -  id of {@link Habit}.
     * @return {@link HabitStatusDto}.
     */
    @ApiOperation(value = "Enroll by habit id that is assigned for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitStatusDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/{id}/status/enroll")
    public ResponseEntity<HabitStatusDto> enrollHabit(@PathVariable Long id,
                                                      @ApiIgnore @CurrentUser UserVO userVO) {
        return ResponseEntity.status(HttpStatus.OK).body(habitStatusService.enrollHabit(id, userVO.getId()));
    }

    /**
     * Method to unenroll {@link HabitStatus} for defined date.
     *
     * @param id   - id of {@link Habit}.
     * @param date - {@link LocalDate} we want to unenroll.
     * @return {@link ResponseEntity}.
     */
    @ApiOperation(value = "Unenroll assigned habit for a specific day.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitStatusDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/{id}/status/unenroll/{date}")
    public ResponseEntity<HabitStatusDto> unenrollHabit(@PathVariable Long id,
                                                        @ApiIgnore @CurrentUser UserVO userVO,
                                                        @PathVariable(value = "date")
                                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        habitStatusService.unenrollHabit(id, userVO.getId(), date);
        return ResponseEntity.ok().build();
    }

    /**
     * Method to enroll {@link HabitStatus} for defined date.
     *
     * @param id   - id of {@link Habit}.
     * @param date - {@link LocalDate} we want to enroll.
     * @return {@link HabitStatusDto}.
     */
    @ApiOperation(value = "Enroll assigned habit for a specific day.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitStatusDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/{id}/status/enroll/{date}")
    public ResponseEntity<HabitStatusDto> enrollHabitInDate(@PathVariable Long id,
                                                            @ApiIgnore @CurrentUser UserVO userVO,
                                                            @PathVariable(value = "date")
                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                LocalDate date) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitStatusService.enrollHabitInDate(id, userVO.getId(), date));
    }

    /**
     * Method to update {@link HabitStatus} for {@link HabitAssign} by {@link Habit}'s id.
     *
     * @param id - id of {@link Habit}.
     * @return {@link HabitStatusDto}.
     */
    @ApiOperation(value = "Update status by habit id that is assigned for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitStatusDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<HabitStatusDto> update(
        @PathVariable Long id,
        @ApiIgnore @CurrentUser UserVO userVO,
        @Valid @RequestBody UpdateHabitStatusDto habitStatusForUpdateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(habitStatusService
            .update(id, userVO.getId(), habitStatusForUpdateDto));
    }
}
