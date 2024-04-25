package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ApiPageable;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.factoftheday.FactOfTheDayDTO;
import greencity.dto.factoftheday.FactOfTheDayTranslationDTO;
import greencity.dto.factoftheday.FactOfTheDayVO;
import greencity.dto.language.LanguageDTO;
import greencity.service.FactOfTheDayService;
import greencity.service.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Locale;

@AllArgsConstructor
@RestController
@RequestMapping("/factoftheday")
public class FactOfTheDayController {
    private FactOfTheDayService factOfTheDayService;
    private LanguageService languageService;

    /**
     * Method which return a random {@link FactOfTheDayVO}.
     *
     * @param locale string code od language example: en
     * @return {@link FactOfTheDayTranslationDTO}
     */
    @Operation(summary = "Get random fact of the day.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = FactOfTheDayTranslationDTO.class))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @GetMapping("/")
    @ApiLocale
    public ResponseEntity<FactOfTheDayTranslationDTO> getRandomFactOfTheDay(
        @Parameter(hidden = true) @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(factOfTheDayService.getRandomFactOfTheDayByLanguage(locale.getLanguage()));
    }

    /**
     * Method which return pageable 0f {@link FactOfTheDayVO}.
     *
     * @return {@link ResponseEntity}
     */
    @ApiPageable
    @Operation(summary = "Get all facts of the day.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = PageableDto.class))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @GetMapping("/all")
    public ResponseEntity<PageableDto<FactOfTheDayDTO>> getAllFactOfTheDay(
        @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(factOfTheDayService.getAllFactsOfTheDay(pageable));
    }

    /**
     * Method which return {@link FactOfTheDayDTO} by given id.
     *
     * @param id of {@link FactOfTheDayVO}
     * @return {@link FactOfTheDayDTO}
     */
    @Operation(summary = "Find fact of the day by given id.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = FactOfTheDayDTO.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @GetMapping("/find")
    public ResponseEntity<FactOfTheDayDTO> findFactOfTheDay(@RequestParam("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(factOfTheDayService.getFactOfTheDayById(id));
    }

    /**
     * Method which return {@link FactOfTheDayDTO} by given id.
     *
     * @return {@link ResponseEntity}
     */
    @Operation(summary = "Get all distinguish languages that exists in DB")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = LanguageDTO.class))))
    })
    @GetMapping("/languages")
    public ResponseEntity<List<LanguageDTO>> getLanguages() {
        return ResponseEntity.status(HttpStatus.OK)
            .body(languageService.getAllLanguages());
    }
}
