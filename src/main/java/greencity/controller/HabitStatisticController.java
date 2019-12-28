package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import greencity.entity.Habit;
import greencity.entity.HabitStatistic;
import greencity.service.impl.HabitStatisticServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/habit")
@AllArgsConstructor
public class HabitStatisticController {
    private final HabitStatisticServiceImpl habitStatisticServiceImpl;

    /**
     * Method for creating {@link HabitStatistic} by {@link Habit} id.
     *
     * @param addHabitStatisticDto - dto for {@link HabitStatistic} entity.
     * @return dto {@link AddHabitStatisticDto} instance.
     * @author Yuriy Olkhovskyi.
     */
    @ApiOperation(value = "Add habit statistic.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = AddHabitStatisticDto.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/statistic/")
    public ResponseEntity<Object> save(@Valid @RequestBody AddHabitStatisticDto addHabitStatisticDto) {
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
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping("/statistic/{habitStatisticId}")
    public ResponseEntity<UpdateHabitStatisticDto> update(
        @PathVariable Long habitStatisticId, @Valid @RequestBody UpdateHabitStatisticDto habitStatisticForUpdateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(habitStatisticServiceImpl
            .update(habitStatisticId, habitStatisticForUpdateDto));
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
