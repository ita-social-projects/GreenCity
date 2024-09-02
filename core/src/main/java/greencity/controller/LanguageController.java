package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.language.LanguageDTO;
import greencity.service.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/languages")
public class LanguageController {
    private final LanguageService languageService;

    /**
     * Method for finding all language.
     *
     * @return list of {@link LanguageDTO}
     */
    @Operation(summary = "Get all distinguish languages that exists in DB")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = LanguageDTO.class))))
    })
    @GetMapping
    public ResponseEntity<List<LanguageDTO>> getLanguages() {
        return ResponseEntity.ok().body(languageService.getAllLanguages());
    }

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
}
