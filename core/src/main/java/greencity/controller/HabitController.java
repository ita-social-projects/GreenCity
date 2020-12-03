package greencity.controller;

import greencity.annotations.*;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.goal.GoalDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.service.HabitService;
import greencity.service.TagsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;
import java.util.Locale;
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
    private final HabitService habitService;
    private final TagsService tagsService;

    /**
     * Method finds {@link HabitVO} by given id with locale translation.
     *
     * @param id     of {@link HabitVO}.
     * @param locale {@link Locale} with needed language code.
     * @return {@link HabitDto}.
     */
    @ApiOperation(value = "Find habit by id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND),
    })
    @GetMapping("/{id}")
    @ApiLocale
    public ResponseEntity<HabitDto> getHabitById(@PathVariable Long id,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitService.getByIdAndLanguageCode(id, locale.getLanguage()));
    }

    /**
     * Method finds all habits that available for tracking for specific language.
     *
     * @param locale   {@link Locale} with needed language code.
     * @param pageable {@link Pageable} instance.
     * @return Pageable of {@link HabitTranslationDto}.
     */
    @ApiOperation(value = "Find all habits.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
    })
    @GetMapping("")
    @ApiPageableWithLocale
    public ResponseEntity<PageableDto<HabitDto>> getAll(
        @ApiIgnore @ValidLanguage Locale locale,
        @ApiIgnore Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(
            habitService.getAllHabitsByLanguageCode(pageable, locale.getLanguage()));
    }

    /**
     * Method finds shoppingList for habit in specific language.
     *
     * @param locale {@link Locale} with needed language code.
     * @param id     {@link Long} with needed habit id.
     * @return List of {@link GoalDto}.
     */
    @ApiOperation(value = "Get shopping list.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
    })
    @GetMapping("{id}/shopping-list")
    @ApiLocale
    public ResponseEntity<List<GoalDto>> getGoals(
        @PathVariable Long id,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK).body(
            habitService.getShoppingListForHabit(id, locale.getLanguage()));
    }

    /**
     * Method finds all habits by tags and language code.
     *
     * @param locale   {@link Locale} with needed language code.
     * @param pageable {@link Pageable} instance.
     * @param tags     {@link List} of {@link String}
     * @return Pageable of {@link HabitTranslationDto}.
     */
    @ApiOperation(value = "Find all habits by tags and language code.")
    @GetMapping("/tags/search")
    @ApiPageableWithLocale
    public ResponseEntity<PageableDto<HabitDto>> getAllByTagsAndLanguageCode(
        @ApiIgnore @ValidLanguage Locale locale,
        @RequestParam List<String> tags,
        @ApiIgnore Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(
            habitService.getAllByTagsAndLanguageCode(pageable, tags, locale.getLanguage()));
    }

    /**
     * The method which returns all habit's tags.
     *
     * @return list of {@link String} (tag's names).
     * @author Markiyan Derevetskyi
     */
    @ApiOperation(value = "Find all habits tags")
    @GetMapping("/tags")
    public ResponseEntity<List<String>> findAllHabitsTags() {
        return ResponseEntity.status(HttpStatus.OK).body(tagsService.findAllHabitsTags());
    }
}
