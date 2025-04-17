package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.language.LanguageVO;
import greencity.service.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/languages")
public class LanguageController {
    private final LanguageService languageService;

    /**
     * Method for finding all language code.
     *
     * @return list of {@link String}
     */
    @Operation(summary = "Get all language code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK)
    })
    @GetMapping("/codes")
    public ResponseEntity<List<String>> getAllLanguageCodes() {
        return ResponseEntity.ok().body(languageService.findAllLanguageCodes());
    }

    /**
     * Method for finding Language by id.
     *
     * @return {@link LanguageVO}
     */
    @Operation(summary = "Find language by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
                    content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
                    content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<LanguageVO> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(languageService.findById(id));
    }

    /**
     * Check whether Language exists by id.
     *
     * @return boolean of whether language exists by that id
     */
    @Operation(summary = "Check whether language exists by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
                    content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
    })
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity.ok().body(languageService.existsById(id));
    }
}
