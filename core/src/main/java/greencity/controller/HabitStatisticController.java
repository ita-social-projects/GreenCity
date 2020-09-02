package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.HabitItemsAmountStatisticDto;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import greencity.entity.Habit;
import greencity.entity.HabitStatistic;
import greencity.service.HabitStatisticService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
    private final HabitStatisticService habitStatisticService;

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
            .body(habitStatisticService.save(addHabitStatisticDto));
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
        return ResponseEntity.status(HttpStatus.OK).body(habitStatisticService
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
            .body(habitStatisticService.findAllByHabitId(habitId));
    }

    /**
     * Returns statistics for all not taken habit items in the system for today.
     * Data is returned as an array of key-value-pairs mapped to {@link HabitItemsAmountStatisticDto},
     * where key is the name of habit item and value is not taken amount of these items.
     * Language of habit items is defined by the `language` parameter.
     *
     * @param language - Name of habit item localization language(e.x. "en" or "uk").
     * @return {@link List} of {@link HabitItemsAmountStatisticDto}s contain those key-value pairs.
     */
    @ApiOperation(value = "Get today's statistic for all habit items")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = List.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/statistic/todayStatisticsForAllHabitItems")
    public ResponseEntity<List<HabitItemsAmountStatisticDto>> getTodayStatisticsForAllHabitItems(
        @ApiParam(value = "Requested language code")
        @RequestParam(defaultValue = "en") String language
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitStatisticService.getTodayStatisticsForAllHabitItems(language));
    }
}
