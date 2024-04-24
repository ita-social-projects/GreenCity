package greencity.controller;

import static greencity.constant.ErrorMessage.INVALID_HABIT_ID;
import greencity.annotations.ApiLocale;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.advice.AdviceDto;
import greencity.dto.advice.AdvicePostDto;
import greencity.dto.advice.AdviceVO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.service.AdviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import java.util.Locale;

@RestController
@RequestMapping("/advices")
@AllArgsConstructor
@Validated
public class AdviceController {
    private final AdviceService adviceService;

    /**
     * The controller which returns random {@link AdviceVO} by HabitDictionary
     * adviceId.
     *
     * @param habitId HabitDictionary
     * @return {@link AdviceDto}
     * @author Vitaliy Dzen
     */
    @Operation(summary = "Get random content by habit adviceId")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = INVALID_HABIT_ID,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @GetMapping("/random/{habitId}")
    @ApiLocale
    public LanguageTranslationDTO getRandomAdviceByHabitIdAndLanguage(
        @PathVariable Long habitId,
        @Parameter(hidden = true) @ValidLanguage Locale locale) {
        return adviceService.getRandomAdviceByHabitIdAndLanguage(habitId, locale.getLanguage());
    }

    /**
     * The controller which returns all {@link AdviceVO}.
     *
     * @return List of {@link AdviceDto}
     * @author Vitaliy Dzen
     */
    @Operation(summary = "Get all advices")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = PageableDto.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @GetMapping
    public PageableDto<AdviceVO> getAll(@Parameter(hidden = true) Pageable pageable) {
        return adviceService.getAllAdvices(pageable);
    }

    /**
     * The controller which returns advice by id {@link AdviceVO}.
     *
     * @return instance of {@link AdviceVO}
     * @author Markiyan Derevetskyi
     */
    @Operation(summary = "Get advice by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @GetMapping("/{id}")
    public AdviceVO getById(@PathVariable Long id) {
        return adviceService.getAdviceById(id);
    }

    /**
     * The controller which saveAdviceAndAdviceTranslation {@link AdviceVO}.
     *
     * @param advice {@link AdviceDto}
     * @return {@link ResponseEntity}
     * @author Vitaliy Dzen
     */
    @Operation(description = "Save advice")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @PostMapping
    public ResponseEntity<AdviceVO> save(@Valid @RequestBody AdvicePostDto advice) {
        AdviceVO response = adviceService.save(advice);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * The controller which update {@link AdviceVO}.
     *
     * @param dto {@link AdviceDto}
     * @return {@link ResponseEntity}
     * @author Vitaliy Dzen
     */
    @Operation(summary = "Update advice")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @PutMapping("/{adviceId}")
    public ResponseEntity<AdvicePostDto> update(
        @Valid @RequestBody AdvicePostDto dto, @PathVariable Long adviceId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(adviceService.update(dto, adviceId));
    }

    /**
     * The controller which delete {@link AdviceVO}.
     *
     * @param adviceId of {@link AdviceVO}
     * @return {@link ResponseEntity}
     * @author Vitaliy Dzen
     */
    @Operation(summary = "Delete content")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @DeleteMapping("/{adviceId}")
    public ResponseEntity<Object> delete(@PathVariable Long adviceId) {
        adviceService.delete(adviceId);
        return ResponseEntity.ok().build();
    }
}
