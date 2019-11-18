package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.CalendarUsefulHabitsDto;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import greencity.entity.HabitStatistic;
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
     * The method which saves new statistic by Habit id.
     *
     * @param addHabitStatisticDto - dto for {@link HabitStatistic} entity.
     * @returndto {@link AddHabitStatisticDto} instance.
     * @author Yuriy Olkhovskyi.
     */
    @ApiOperation(value = "Add HabitStatistic.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = AddHabitStatisticDto.class),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/statistic")
    public ResponseEntity saveHabitStatistic(@Valid @RequestBody AddHabitStatisticDto addHabitStatisticDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(habitStatisticServiceImpl.save(addHabitStatisticDto));
    }

    /**
     * The method which updates {@link HabitStatistic}.
     *
     * @param habitStatisticForUpdateDto - {@link UpdateHabitStatisticDto} with habit statistic id and
     *                                   updated rate and amount of items.
     * @return response object with {@link UpdateHabitStatisticDto}.
     */
    @ApiOperation(value = "Update habit statistic.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping("/statistic/{habitStatisticId}")
    public ResponseEntity<UpdateHabitStatisticDto> update(@PathVariable Long habitStatisticId,
                                                          @Valid @RequestBody UpdateHabitStatisticDto habitStatisticForUpdateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(habitStatisticServiceImpl
            .update(habitStatisticId, habitStatisticForUpdateDto));
    }

    /**
     * dsad.
     *
     * @param principal dasdas.
     * @return
     */
    @ApiOperation(value = "Find user statistic")
    @GetMapping("/statistic")
    public ResponseEntity<CalendarUsefulHabitsDto> findAllBy(@ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitStatisticServiceImpl.findAllStatistic(principal.getName()));
    }

    /**
     * dasdasdas.
     *
     * @param habitId dsadasdas.
     * @return
     */
    @ApiOperation(value = "Find statistic by its id.")
    @GetMapping("/statistic/{habitId}")
    public ResponseEntity<List<HabitStatisticDto>> findAllByUserEmail(
        @PathVariable Long habitId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitStatisticServiceImpl.findAllByHabitId(habitId));
    }
}
