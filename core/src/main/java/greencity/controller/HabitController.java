package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ApiPageable;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.HabitItemsAmountStatisticDto;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.Habit;
import greencity.entity.HabitStatistic;
import greencity.entity.User;
import greencity.service.HabitAssignService;
import greencity.service.HabitService;
import greencity.service.HabitStatisticService;
import greencity.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final UserService userService;
    private final HabitService habitService;

    /**
     * Method which assign habit for {@link User}.
     *
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
            .body(habitAssignService.assignHabitForUser(habitId, user));
    }

    /**
     * Method returns all habits, available for tracking for specific language.
     *
     * @param locale needed language code
     * @return Pageable of {@link HabitTranslationDto}
     */
    @ApiOperation(value = "Get all habits.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("")
    @ApiPageable
    @ApiLocale
    public ResponseEntity<PageableDto<HabitTranslationDto>> getAll(
        @ApiIgnore Pageable pageable,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK).body(
            habitService.getAllHabitsByLanguageCode(pageable, locale.getLanguage()));
    }

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
    @ApiOperation(value = "Get today's statistic for all habit items")
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
