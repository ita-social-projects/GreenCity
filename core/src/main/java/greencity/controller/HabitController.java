package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ApiPageable;
import greencity.annotations.CurrentUser;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitAssignStatDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.HabitItemsAmountStatisticDto;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatistic;
import greencity.entity.User;
import greencity.service.HabitAssignService;
import greencity.service.HabitService;
import greencity.service.HabitStatisticService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.Locale;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/habit")
public class HabitController {
    private final HabitStatisticService habitStatisticService;
    private final HabitAssignService habitAssignService;
    private final HabitService habitService;

    /**
     * Method finds {@link Habit} by given id with locale translation.
     *
     * @param id of {@link Habit}.
     * @return {@link HabitDto}.
     */
    @ApiOperation(value = "Find habit by id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/{id}")
    public ResponseEntity<HabitDto> getHabitById(@PathVariable Long id,
                                                 @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitService.getByIdAndLanguageCode(id, locale.getLanguage()));
    }

    /**
     * Method finds all habits that available for tracking for specific language.
     *
     * @param locale {@link Locale} with needed language code.
     * @return Pageable of {@link HabitTranslationDto}.
     */
    @ApiOperation(value = "Find all habits.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("")
    @ApiPageable
    @ApiLocale
    public ResponseEntity<PageableDto<HabitDto>> getAll(
        @ApiIgnore Pageable pageable,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK).body(
            habitService.getAllHabitsByLanguageCode(pageable, locale.getLanguage()));
    }

    /**
     * Method which assign habit for {@link User}.
     *
     * @param habitId - id of {@link Habit}
     * @return {@link ResponseEntity}
     */
    @ApiOperation(value = "Assign habit for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/assign/{habitId}")
    public ResponseEntity<Object> assign(@PathVariable Long habitId,
                                         @ApiIgnore @CurrentUser User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(habitAssignService.assignHabitForUser(habitId, user));
    }

    /**
     * Method return {@link HabitAssign} by it's id.
     *
     * @param habitAssignId - id of {@link HabitAssign}
     * @return {@link HabitAssignDto}
     */
    @ApiOperation(value = "Get habit assign.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/assign/{habitAssignId}")
    public ResponseEntity<HabitAssignDto> getHabitAssign(@PathVariable Long habitAssignId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService.getById(habitAssignId));
    }

    /**
     * Method to update {@link HabitAssign} for it's id.
     *
     * @param habitAssignId - id of {@link HabitAssign}.
     * @return {@link HabitAssignDto}.
     */
    @ApiOperation(value = "Update habit assign acquired or suspended status.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping("assign/{habitAssignId}")
    public ResponseEntity<HabitAssignDto> updateAssign(
        @PathVariable Long habitAssignId, @Valid @RequestBody HabitAssignStatDto habitAssignStatDto) {
        return ResponseEntity.status(HttpStatus.OK).body(habitAssignService
            .updateStatus(habitAssignId, habitAssignStatDto));
    }

    /**
     * Method for creating {@link HabitStatistic} for user {@link HabitAssign}.
     *
     * @param addHabitStatisticDto - dto for {@link HabitStatistic} entity.
     * @return dto {@link AddHabitStatisticDto} instance.
     * @author Yuriy Olkhovskyi.
     */
    @ApiOperation(value = "Add habit statistic for assigned habit.")
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
     * Method for updating {@link HabitStatistic} by it's id.
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
    public ResponseEntity<UpdateHabitStatisticDto> updateStatistic(
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
        return ResponseEntity.status(HttpStatus.OK).body(habitStatisticService.findAllStatsByHabitId(habitId));
    }

    /**
     * Returns statistics for all not taken habit items in the system for today.
     * Data is returned as an array of key-value-pairs mapped to {@link HabitItemsAmountStatisticDto},
     * where key is the name of habit item and value is not taken amount of these items.
     * Language of habit items is defined by the `language` parameter.
     *
     * @param locale - Name of habit item localization language(e.x. "en" or "uk").
     * @return {@link List} of {@link HabitItemsAmountStatisticDto}s contain those key-value pairs.
     */
    @ApiOperation(value = "Get today's statistic for all habit items.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = List.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/statistic/todayStatisticsForAllHabitItems")
    @ApiLocale
    public ResponseEntity<List<HabitItemsAmountStatisticDto>> getTodayStatisticsForAllHabitItems(
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitStatisticService.getTodayStatisticsForAllHabitItems(locale.getLanguage()));
    }
}
