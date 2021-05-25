package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ApiPageable;
import greencity.annotations.ValidLanguage;
import greencity.client.RestClient;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.factoftheday.FactOfTheDayDTO;
import greencity.dto.factoftheday.FactOfTheDayTranslationDTO;
import greencity.dto.factoftheday.FactOfTheDayVO;
import greencity.dto.language.LanguageDTO;
import greencity.service.FactOfTheDayService;
import greencity.service.LanguageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Locale;

import static greencity.enums.EmailNotification.*;

@AllArgsConstructor
@RestController
@RequestMapping("/factoftheday")
public class FactOfTheDayController {
    private FactOfTheDayService factOfTheDayService;
    private LanguageService languageService;
    private RestClient restClient;

    /**
     * Method which return a random {@link FactOfTheDayVO}.
     *
     * @param locale string code od language example: en
     * @return {@link FactOfTheDayTranslationDTO}
     */
    @ApiOperation(value = "Get random fact of the day.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = FactOfTheDayTranslationDTO.class),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/")
    @ApiLocale
    public ResponseEntity<FactOfTheDayTranslationDTO> getRandomFactOfTheDay(@ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(factOfTheDayService.getRandomFactOfTheDayByLanguage(locale.getLanguage()));
    }

    /**
     * Method which return pageable 0f {@link FactOfTheDayVO}.
     *
     * @return {@link ResponseEntity}
     */
    @ApiPageable
    @ApiOperation(value = "Get all facts of the day.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = PageableDto.class),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/all")
    public ResponseEntity<PageableDto<FactOfTheDayDTO>> getAllFactOfTheDay(@ApiIgnore Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(factOfTheDayService.getAllFactsOfTheDay(pageable));
    }

    /**
     * Method which return {@link FactOfTheDayDTO} by given id.
     *
     * @param id of {@link FactOfTheDayVO}
     * @return {@link FactOfTheDayDTO}
     */
    @ApiOperation(value = "Find fact of the day by given id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = FactOfTheDayDTO.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
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
    @ApiOperation(value = "Get all distinguish languages that exists in DB")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK)
    })
    @GetMapping("/languages")
    public ResponseEntity<List<LanguageDTO>> getLanguages() {
        return ResponseEntity.status(HttpStatus.OK)
            .body(languageService.getAllLanguages());
    }
}
