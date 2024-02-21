package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.service.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/language")
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
    @GetMapping("")
    public ResponseEntity<List<String>> getAllLanguageCodes() {
        return ResponseEntity.status(HttpStatus.OK).body(
            languageService.findAllLanguageCodes());
    }
}
