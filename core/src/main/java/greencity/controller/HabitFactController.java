package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ApiPageableWithLocale;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.habitfact.HabitFactDto;
import greencity.dto.habitfact.HabitFactDtoResponse;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactUpdateDto;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.service.HabitFactService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import java.util.Locale;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/facts")
@AllArgsConstructor
public class HabitFactController {
    private final HabitFactService habitFactService;
    private final ModelMapper mapper;

    /**
     * The controller which returns random {@link HabitFactVO} by {@link HabitVO}
     * id.
     *
     * @param habitId {@link HabitVO} id.
     * @return {@link HabitFactDto}.
     * @author Vitaliy Dzen
     */
    @Operation(summary = "Get random habit fact by habit id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER,
            content = @Content(examples = @ExampleObject(HttpStatuses.SEE_OTHER))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @GetMapping("/random/{habitId}")
    @ApiLocale
    public LanguageTranslationDTO getRandomFactByHabitId(
        @PathVariable Long habitId,
        @Parameter(hidden = true) @ValidLanguage Locale locale) {
        return habitFactService.getRandomHabitFactByHabitIdAndLanguage(habitId, locale.getLanguage());
    }

    /**
     * The controller which return today's {@link HabitFactVO} of the day.
     *
     * @param languageId id of language to display the {@link HabitFactVO}.
     * @return {@link LanguageTranslationDTO} of today's {@link HabitFactVO} of the
     *         day.
     */
    @Operation(summary = "Get habit fact of the day")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER,
            content = @Content(examples = @ExampleObject(HttpStatuses.SEE_OTHER))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @GetMapping("/dayFact/{languageId}")
    public LanguageTranslationDTO getHabitFactOfTheDay(
        @PathVariable Long languageId) {
        return habitFactService.getHabitFactOfTheDay(languageId);
    }

    /**
     * The controller which returns all {@link HabitFactVO}.
     *
     * @return List of {@link HabitFactDto}.
     * @author Vitaliy Dzen
     */
    @Operation(summary = "Get all facts")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER,
            content = @Content(examples = @ExampleObject(HttpStatuses.SEE_OTHER))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @GetMapping
    @ApiPageableWithLocale
    public ResponseEntity<PageableDto<LanguageTranslationDTO>> getAll(@Parameter(hidden = true) Pageable page,
        @Parameter(hidden = true) @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitFactService.getAllHabitFacts(page, locale.getLanguage()));
    }

    /**
     * The controller which save {@link HabitFactVO}.
     *
     * @param fact {@link HabitFactPostDto}.
     * @return {@link ResponseEntity}.
     * @author Vitaliy Dzen
     */
    @Operation(summary = "Save habit fact")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED),
        @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER,
            content = @Content(examples = @ExampleObject(HttpStatuses.SEE_OTHER))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @PostMapping
    public ResponseEntity<HabitFactDtoResponse> save(@Valid @RequestBody HabitFactPostDto fact) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            mapper.map(habitFactService.save(fact), HabitFactDtoResponse.class));
    }

    /**
     * The controller which update {@link HabitFactVO}.
     *
     * @param dto {@link HabitFactPostDto}.
     * @param id  of {@link HabitFactVO}.
     * @return {@link ResponseEntity}.
     * @author Vitaliy Dzen
     */
    @Operation(summary = "Update habit fact")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER,
            content = @Content(examples = @ExampleObject(HttpStatuses.SEE_OTHER))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<HabitFactPostDto> update(
        @Valid @RequestBody HabitFactUpdateDto dto, @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(mapper.map(habitFactService.update(dto, id), HabitFactPostDto.class));
    }

    /**
     * The controller which delete {@link HabitFactVO}.
     *
     * @param id of {@link HabitFactVO}.
     * @return {@link ResponseEntity}.
     * @author Vitaliy Dzen
     */
    @Operation(summary = "Delete habit fact")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER,
            content = @Content(examples = @ExampleObject(HttpStatuses.SEE_OTHER))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        habitFactService.delete(id);
        return ResponseEntity.ok().build();
    }
}
