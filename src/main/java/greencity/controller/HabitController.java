package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.habitstatistic.HabitStatisticForUpdateDto;
import greencity.entity.HabitStatistic;
import greencity.service.impl.HabitStatisticServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/habit")
@AllArgsConstructor
public class HabitController {
    private final HabitStatisticServiceImpl habitStatisticServiceImpl;

    /**
     * The method which saves new statistic by Habit id.
     *
     * @param habitStatisticDto - dto for {@link HabitStatistic} entity.
     * @returndto {@link HabitStatisticDto} instance.
     * @author Yuriy Olkhovskyi.
     */
    @ApiOperation(value = "Add HabitStatistic.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = HabitStatisticDto.class),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/statistic")
    public ResponseEntity saveHabitStatistic(@Valid @RequestBody HabitStatisticDto habitStatisticDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(habitStatisticServiceImpl.save(habitStatisticDto));
    }

    /**
     * The method which updates {@link HabitStatistic}.
     *
     * @param habitStatisticForUpdateDto - {@link HabitStatisticForUpdateDto} with habit statistic id and
     *                                  updated rate and amount of items.
     * @return response object with {@link HabitStatisticForUpdateDto}.
     */
    @ApiOperation(value = "Update habit statistic.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PutMapping("/statistic/update")
    public ResponseEntity<HabitStatisticForUpdateDto> update(
        @Valid @RequestBody HabitStatisticForUpdateDto habitStatisticForUpdateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(habitStatisticServiceImpl
            .update(habitStatisticForUpdateDto));
    }
}
