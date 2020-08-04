package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.habitstatus.HabitStatusDto;
import greencity.entity.User;
import greencity.service.HabitStatusService;
import greencity.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/habit/status")
public class HabitStatusController {
    private HabitStatusService habitStatusService;
    private UserService userService;

    /**
     * Method return {@link greencity.entity.HabitStatus} for user by habit
     * @param habitId - id of habit
     * @return {@link HabitStatusDto}
     */
    @ApiOperation(value = "Get habit status for user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/{habitId}")
    public ResponseEntity<HabitStatusDto> getHabitStatusForUser(@PathVariable Long habitId, @ApiIgnore
    @AuthenticationPrincipal
        Principal principal) {
        User user = userService.findByEmail(principal.getName());

        return ResponseEntity.status(HttpStatus.OK)
            .body(habitStatusService.findStatusByHabitIdAndUserId(habitId, user.getId()));
    }

    /**
     * Method enroll {@link greencity.entity.Habit}
     * @param habitId - id of habit which we enroll
     * @return {@link HabitStatusDto}
     */
    @ApiOperation(value = "Enroll habit.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/enroll/{habitId}")
    public ResponseEntity<HabitStatusDto> enrollHabit(@PathVariable Long habitId, @ApiIgnore
    @AuthenticationPrincipal
        Principal principal) {
        User user = userService.findByEmail(principal.getName());
        return ResponseEntity.status(HttpStatus.OK).body(habitStatusService.enrollHabit(habitId, user.getId()));
    }

    /**
     * Method unenroll Habit in defined date
     * @param habitId - id of habit
     * @param date - date we want unenroll
     * @return {@link ResponseEntity}
     */
    @ApiOperation(value = "Unenroll habit.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/unenroll/{habitId}/{date}")
    public ResponseEntity<HabitStatusDto> unenrollHabit(@PathVariable Long habitId,
                                                        @PathVariable(value = "date")
                                                        @DateTimeFormat(pattern = "MM-dd-yyyy") Date date,
                                                        @ApiIgnore
                                                        @AuthenticationPrincipal
                                                            Principal principal) {
        User user = userService.findByEmail(principal.getName());
        LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        habitStatusService.unenrollHabit(ldt, habitId, user.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * Method enroll habit for defined date
     * @param habitId - id of habit
     * @param date - date we want enroll
     * @return {@link HabitStatusDto}
     */
    @ApiOperation(value = "Enroll for a specific day.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/enroll/{habitId}/{date}")
    public ResponseEntity<HabitStatusDto> enrollHabitInDate(@PathVariable Long habitId,
                                                        @PathVariable(value = "date")
                                                        @DateTimeFormat(pattern = "MM-dd-yyyy") Date date,
                                                        @ApiIgnore
                                                        @AuthenticationPrincipal
                                                            Principal principal) {
        User user = userService.findByEmail(principal.getName());
        LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        habitStatusService.enrollHabitInDate(habitId, user.getId(), ldt);
        return ResponseEntity.ok().build();
    }
}