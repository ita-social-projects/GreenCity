package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.entity.Habit;
import greencity.entity.HabitStatistic;
import greencity.entity.User;
import greencity.service.HabitService;
import greencity.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/habit")
public class HabitController {
    private HabitService habitService;
    private UserService userService;

    /**
     * Method which assign habit for {@link User}
     * @param habitId - id of {@link Habit}
     * @return {@link ResponseEntity}
     */
    @ApiOperation(value = "Assign habit.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = AddHabitStatisticDto.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/assign/{habitId}")
    public ResponseEntity<Object> assign(@PathVariable Long habitId, @ApiIgnore @AuthenticationPrincipal
        Principal principal) {
        User user = userService.findByEmail(principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(habitService.assignHabitForUser(habitId, user));
    }
}
