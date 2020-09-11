package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ApiPageable;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.factoftheday.FactOfTheDayDTO;
import greencity.dto.factoftheday.FactOfTheDayPostDTO;
import greencity.dto.factoftheday.FactOfTheDayTranslationDTO;
import greencity.dto.genericresponse.FieldErrorDto;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.dto.language.LanguageDTO;
import greencity.entity.FactOfTheDay;
import greencity.service.FactOfTheDayService;
import greencity.service.FactOfTheDayTranslationService;
import greencity.service.LanguageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@AllArgsConstructor
@RestController
@RequestMapping("/factoftheday")
public class FactOfTheDayController {
    private FactOfTheDayService factOfTheDayService;
    private FactOfTheDayTranslationService factOfTheDayTranslationService;
    private LanguageService languageService;

    /**
     * Method which return a random {@link FactOfTheDay}.
     *
     * @param locale string code od language example: en
     * @return {@link FactOfTheDayTranslationDTO}
     */
    @ApiOperation(value = "Get random fact of the day.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = FactOfTheDayTranslationDTO.class),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/")
    @ApiLocale
    public ResponseEntity<FactOfTheDayTranslationDTO> getRandomFactOfTheDay(@ApiIgnore @AuthenticationPrincipal
                                                                                Principal principal,
                                                                            @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(factOfTheDayTranslationService.getRandomFactOfTheDayByLanguage(locale.getLanguage()));
    }

    /**
     * Method which return pageable 0f {@link FactOfTheDay}.
     *
     * @return {@link ResponseEntity}
     */
    @ApiPageable
    @ApiOperation(value = "Get all facts of the day.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = PageableDto.class),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/all")
    public ResponseEntity<PageableDto<FactOfTheDayDTO>> getAllFactOfTheDay(@ApiIgnore @AuthenticationPrincipal
                                                                               Principal principal, @ApiIgnore
                                                                               Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(factOfTheDayService.getAllFactsOfTheDay(pageable));
    }

    /**
     * Method which return {@link FactOfTheDayDTO} by given id.
     *
     * @param id of {@link FactOfTheDay}
     * @return {@link FactOfTheDayDTO}
     */
    @ApiOperation(value = "Find fact of the day by given id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = FactOfTheDayDTO.class),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/find")
    public ResponseEntity<FactOfTheDayDTO> findFactOfTheDay(@ApiIgnore @AuthenticationPrincipal
                                                                Principal principal, @RequestParam("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(factOfTheDayService.getFactOfTheDayById(id));
    }

    /**
     * Method which saves {@link FactOfTheDay}.
     *
     * @param factOfTheDayPostDTO of {@link FactOfTheDayPostDTO}
     * @return {@link GenericResponseDto} with of operation and errors fields
     */
    @ApiOperation(value = "Save fact of the day")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = GenericResponseDto.class),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @ResponseBody
    @PostMapping("/")
    public GenericResponseDto saveFactOfTheDay(@Valid @RequestBody FactOfTheDayPostDTO factOfTheDayPostDTO,
                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            GenericResponseDto genericResponseDto = new GenericResponseDto();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                genericResponseDto.getErrors().add(
                    new FieldErrorDto(fieldError.getField(), fieldError.getDefaultMessage()));
            }
            return genericResponseDto;
        }
        factOfTheDayService.saveFactOfTheDayAndTranslations(factOfTheDayPostDTO);
        return GenericResponseDto.builder().errors(new ArrayList<>()).build();
    }

    /**
     * Method which updates {@link FactOfTheDay}.
     *
     * @param factOfTheDayPostDTO of {@link FactOfTheDayPostDTO}
     * @return {@link GenericResponseDto} with of operation and errors fields
     */
    @ApiOperation(value = "Update fact of the day")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @ResponseBody
    @PutMapping("/")
    public GenericResponseDto updateFactOfTheDay(@ApiIgnore @AuthenticationPrincipal
                                                     Principal principal,
                                                 @Valid @RequestBody FactOfTheDayPostDTO factOfTheDayPostDTO,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            GenericResponseDto genericResponseDto = new GenericResponseDto();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                genericResponseDto.getErrors().add(
                    new FieldErrorDto(fieldError.getField(), fieldError.getDefaultMessage()));
            }
            return genericResponseDto;
        }
        factOfTheDayService.updateFactOfTheDayAndTranslations(factOfTheDayPostDTO);
        return GenericResponseDto.builder().errors(new ArrayList<>()).build();
    }

    /**
     * Method which return {@link FactOfTheDayDTO} by given id.
     *
     * @return {@link ResponseEntity}
     */
    @ApiOperation(value = "Get all distinguish languages that exists in DB")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/languages")
    public ResponseEntity<List<LanguageDTO>> getLanguages(@ApiIgnore @AuthenticationPrincipal
                                                              Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(languageService.getAllLanguages());
    }

    /**
     * Method which deteles {@link FactOfTheDay} and {@link greencity.entity.FactOfTheDayTranslation} by given id.
     *
     * @param id of Fact of the day
     * @return {@link ResponseEntity}
     */
    @ApiOperation(value = "Delete Fact of the day")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @DeleteMapping("/")
    public ResponseEntity<Long> delete(@ApiIgnore @AuthenticationPrincipal
                                           Principal principal, @RequestParam("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(factOfTheDayService.deleteFactOfTheDayAndTranslations(id));
    }

    /**
     * Method which deteles {@link FactOfTheDay} and {@link greencity.entity.FactOfTheDayTranslation} by given id.
     *
     * @param listId list of IDs
     * @return {@link ResponseEntity}
     */
    @ApiOperation(value = "Get all Fact of the day by given IDs")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @DeleteMapping("/deleteAll")
    public ResponseEntity<List<Long>> deleteAll(@ApiIgnore @AuthenticationPrincipal
                                                    Principal principal, @RequestBody List<Long> listId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(factOfTheDayService.deleteAllFactOfTheDayAndTranslations(listId));
    }
}
