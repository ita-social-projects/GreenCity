package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.user.UserVO;
import greencity.service.AIService;
import greencity.service.AcceptLanguageDisplayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@AllArgsConstructor
@Slf4j
public class AIController {
    private final AIService aiService;
    private final AcceptLanguageDisplayService acceptLanguageDisplayService;


    @Operation(summary = "Makes predictions about the environmental impact of the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @GetMapping("/forecast")
    public ResponseEntity<String> forecast(@Parameter(hidden = true) @CurrentUser UserVO userVO) {
        String language = acceptLanguageDisplayService.resolveLanguage();
        String forecast = aiService.getForecast(userVO.getId(), language);
        return ResponseEntity.ok(forecast);
    }


    @Operation(summary = "Generates news content based on the specified language and query")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @GetMapping("/generate/eco-news")
    public ResponseEntity<String> creatingEcoNews(@RequestParam(required = false) String query) {
        String language = acceptLanguageDisplayService.resolveLanguage();
        String news = aiService.getNews(language, query);
        return ResponseEntity.status(HttpStatus.OK).body(news);
    }

    /**
     * Endpoint for generating eco news based on the user's habits.
     * <p>
     * This method:
     * <ul>
     *   <li>Resolves the user's preferred language from the "Accept-Language" header</li>
     *   <li>Delegates the eco news generation to the AI service</li>
     *   <li>Returns the generated eco news text</li>
     * </ul>
     *
     * @return a ResponseEntity containing the generated eco news text
     */
    @Operation(summary = "Generate eco news based on habits")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @ApiLocale
    @PostMapping("/generate")
    public ResponseEntity<String> generateEcoNewsBasedOnHabits() {
        String language = acceptLanguageDisplayService.resolveLanguage();
        return ResponseEntity.status(HttpStatus.OK)
            .body(aiService.generateEcoNewsBasedOnHabits(language));
    }
}
