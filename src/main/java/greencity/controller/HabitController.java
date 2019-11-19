package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.CalendarUsefulHabitsDto;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import greencity.entity.Habit;
import greencity.entity.HabitStatistic;
import greencity.entity.User;
import greencity.service.impl.HabitStatisticServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/habit")
@AllArgsConstructor
public class HabitController {
    private final HabitStatisticServiceImpl habitStatisticServiceImpl;

    /**
     * Method for creating {@link HabitStatistic} by {@link Habit} id.
     *
     * @param addHabitStatisticDto - dto for {@link HabitStatistic} entity.
     * @returndto {@link AddHabitStatisticDto} instance.
     * @author Yuriy Olkhovskyi.
     */
    @ApiOperation(value = "Add habit statistic.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = AddHabitStatisticDto.class),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/statistic/")
    public ResponseEntity saveHabitStatistic(@Valid @RequestBody AddHabitStatisticDto addHabitStatisticDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(habitStatisticServiceImpl.save(addHabitStatisticDto));
    }

    /**
     * Method for updating {@link HabitStatistic} by its id.
     *
     * @param habitStatisticForUpdateDto - {@link UpdateHabitStatisticDto} with habit statistic id and
     *                                   updated rate and amount of items.
     * @return {@link UpdateHabitStatisticDto} instance.
     */
    @ApiOperation(value = "Update habit statistic.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping("/statistic/{habitStatisticId}")
    public ResponseEntity<UpdateHabitStatisticDto> update(
        @PathVariable Long habitStatisticId, @Valid @RequestBody UpdateHabitStatisticDto habitStatisticForUpdateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(habitStatisticServiceImpl
            .update(habitStatisticId, habitStatisticForUpdateDto));
    }

    /**
     * Method for finding some statistics by {@link User} email.
     *
     * @param email {@link User} email.
     * @return {@link CalendarUsefulHabitsDto} instance.
     */

    /**
     * Method for finding {@link CalendarUsefulHabitsDto} by {@link User} email.
     * Parameter principal are ignored because Spring automatically provide the Principal object.
     *
     * @param principal - Principal with {@link User} email.
     * @return {@link CalendarUsefulHabitsDto} instance.
     */
    @ApiOperation(value = "Find some information about user habits.")
    @GetMapping("/statistic")
    public ResponseEntity<CalendarUsefulHabitsDto> findInfoAboutUserHabits(@ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitStatisticServiceImpl.getInfoAboutUserHabits(principal.getName()));
    }

    /**
     * Method for finding all {@link HabitStatisticDto} by {@link Habit id}.
     *
     * @param habitId {@link Habit} id.
     * @return list of {@link HabitStatisticDto} instances.
     */
    @ApiOperation(value = "Find statistic by habit id.")
    @GetMapping("/statistic/{habitId}")
    public ResponseEntity<List<HabitStatisticDto>> findAllByHabitId(
        @PathVariable Long habitId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitStatisticServiceImpl.findAllByHabitId(habitId));
    }
}
