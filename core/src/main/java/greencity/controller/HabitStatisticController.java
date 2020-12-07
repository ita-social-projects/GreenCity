package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.CurrentUser;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habit.HabitVO;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.HabitItemsAmountStatisticDto;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import greencity.dto.user.UserVO;
import greencity.service.HabitStatisticService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.Locale;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/habit/statistic")
public class HabitStatisticController {
    private final HabitStatisticService habitStatisticService;

    /**
     * Method for finding all {@link HabitStatisticDto} by {@link HabitVO id}.
     *
     * @param habitId {@link HabitVO} id.
     * @return list of {@link HabitStatisticDto} instances.
     */
    @ApiOperation(value = "Find all statistics by habit id.")
    @GetMapping("/{habitId}")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = List.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    public ResponseEntity<List<HabitStatisticDto>> findAllByHabitId(
        @PathVariable Long habitId) {
        return ResponseEntity.status(HttpStatus.OK).body(habitStatisticService.findAllStatsByHabitId(habitId));
    }

    /**
     * Method for finding {@link HabitStatisticDto} by {@link HabitAssignVO id}.
     *
     * @param habitAssignId {@link HabitAssignVO} id.
     * @return list of {@link HabitStatisticDto} instances.
     */
    @ApiOperation(value = "Find all statistics by habit assign id.")
    @GetMapping("/assign/{habitAssignId}")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = List.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    public ResponseEntity<List<HabitStatisticDto>> findAllStatsByHabitAssignId(
        @PathVariable Long habitAssignId) {
        return ResponseEntity.status(HttpStatus.OK).body(
            habitStatisticService.findAllStatsByHabitAssignId(habitAssignId));
    }

    /**
     * Method for creating {@link HabitStatisticDto} by {@link HabitVO} id that is
     * assigned for current user.
     *
     * @param addHabitStatisticDto dto for {@link HabitStatisticDto} entity.
     * @param userVO               {@link UserVO} instance.
     * @param habitId              {@link HabitVO} id.
     * @return dto {@link AddHabitStatisticDto} instance.
     * @author Yuriy Olkhovskyi.
     */
    @ApiOperation(value = "add statistic by habit id that is assigned for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = HabitStatisticDto.class),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/{habitId}")
    public ResponseEntity<HabitStatisticDto> saveHabitStatistic(
        @Valid @RequestBody AddHabitStatisticDto addHabitStatisticDto,
        @ApiIgnore @CurrentUser UserVO userVO,
        @PathVariable Long habitId) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(habitStatisticService.saveByHabitIdAndUserId(habitId, userVO.getId(), addHabitStatisticDto));
    }

    /**
     * Method for updating {@link HabitStatisticDto} by it's id.
     *
     * @param id                         {@link HabitStatisticDto} id.
     * @param habitStatisticForUpdateDto {@link UpdateHabitStatisticDto} with habit
     *                                   statistic id and updated rate and amount of
     *                                   items.
     * @return {@link UpdateHabitStatisticDto} instance.
     */
    @ApiOperation(value = "Update habit statistic.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitStatisticDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PutMapping("/{id}")
    public ResponseEntity<UpdateHabitStatisticDto> updateStatistic(
        @PathVariable Long id,
        @ApiIgnore @CurrentUser UserVO userVO,
        @Valid @RequestBody UpdateHabitStatisticDto habitStatisticForUpdateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(habitStatisticService
            .update(id, userVO.getId(), habitStatisticForUpdateDto));
    }

    /**
     * Returns statistics for all not taken habit items in the system for today.
     * Data is returned as an array of key-value-pairs mapped to
     * {@link HabitItemsAmountStatisticDto}, where key is the name of habit item and
     * value is not taken amount of these items. Language of habit items is defined
     * by the `language` parameter.
     *
     * @param locale - Name of habit item localization language(e.x. "en" or "ua").
     * @return {@link List} of {@link HabitItemsAmountStatisticDto}s contain those
     *         key-value pairs.
     */
    @ApiOperation(value = "Get today's statistic for all habit items.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = List.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("/todayStatisticsForAllHabitItems")
    @ApiLocale
    public ResponseEntity<List<HabitItemsAmountStatisticDto>> getTodayStatisticsForAllHabitItems(
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitStatisticService.getTodayStatisticsForAllHabitItems(locale.getLanguage()));
    }
}
