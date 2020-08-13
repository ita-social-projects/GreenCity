package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.factoftheday.FactOfTheDayTranslationDTO;
import greencity.entity.FactOfTheDay;
import greencity.service.FactOfTheDayService;
import greencity.service.FactOfTheDayTranslationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/factoftheday")
public class FactOfTheDayController {
    private FactOfTheDayService factOfTheDayService;
    private FactOfTheDayTranslationService factOfTheDayTranslationService;

    /**
     * Method which return a random {@link FactOfTheDay}.
     *
     * @return {@link ResponseEntity}
     */
    @ApiOperation(value = "Get fact of the day.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = FactOfTheDayTranslationDTO.class),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/")
    public ResponseEntity<FactOfTheDayTranslationDTO> getFactOfTheDay(@ApiIgnore @AuthenticationPrincipal
                                                      Principal principal, @RequestParam String languageCode) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(factOfTheDayTranslationService.getRandomFactOfTheDayByLanguage(languageCode));
    }
}
