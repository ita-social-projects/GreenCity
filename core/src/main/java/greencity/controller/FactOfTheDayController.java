package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.factoftheday.FactOfTheDayTranslationDTO;
import greencity.dto.factoftheday.FactOfTheDayVO;
import greencity.service.FactOfTheDayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fact-of-the-day")
public class FactOfTheDayController {
    private final FactOfTheDayService factOfTheDayService;

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
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @ApiLocale
    @GetMapping("/random")
    public ResponseEntity<FactOfTheDayTranslationDTO> getRandomFactOfTheDay(
        @Parameter(hidden = true) @ValidLanguage Locale locale) {
        return ResponseEntity.ok().body(factOfTheDayService.getRandomFactOfTheDayByLanguage(locale.getLanguage()));
    }
}
