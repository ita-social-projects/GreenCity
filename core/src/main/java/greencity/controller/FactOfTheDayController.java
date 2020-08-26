package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.errorsresponse.ErrorsResponseDto;
import greencity.dto.errorsresponse.FieldErrorDto;
import greencity.dto.factoftheday.FactOfTheDayDTO;
import greencity.dto.factoftheday.FactOfTheDayPostDTO;
import greencity.dto.factoftheday.FactOfTheDayTranslationDTO;
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
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
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
     * @param languageCode string code od language example: en
     * @return {@link FactOfTheDayTranslationDTO}
     */
    @ApiOperation(value = "Get random fact of the day.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = FactOfTheDayTranslationDTO.class),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/")
    public ResponseEntity<FactOfTheDayTranslationDTO> getRandomFactOfTheDay(@ApiIgnore @AuthenticationPrincipal
                                                                          Principal principal,
                                                                            @RequestParam String languageCode) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(factOfTheDayTranslationService.getRandomFactOfTheDayByLanguage(languageCode));
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
     * @return {@link ErrorsResponseDto} with of operation and errors fields
     */
    @ApiOperation(value = "Save fact of the day")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = ErrorsResponseDto.class),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @ResponseBody
    @PostMapping("/")
    public ErrorsResponseDto saveFactOfTheDay(@Valid @RequestBody FactOfTheDayPostDTO factOfTheDayPostDTO,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ErrorsResponseDto errorsResponseDto = new ErrorsResponseDto();
            errorsResponseDto.setStatus(false);
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorsResponseDto.getErrors().add(
                    new FieldErrorDto(fieldError.getField(), fieldError.getDefaultMessage()));
            }
            return errorsResponseDto;
        }
        factOfTheDayService.saveFactOfTheDayAndTranslations(factOfTheDayPostDTO);
        return new ErrorsResponseDto(true, new ArrayList<>());
    }

    /**
     * Method which updates {@link FactOfTheDay}.
     *
     * @param factOfTheDayPostDTO of {@link FactOfTheDayPostDTO}
     * @return {@link ErrorsResponseDto} with of operation and errors fields
     */
    @ApiOperation(value = "Update fact of the day")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @ResponseBody
    @PutMapping("/")
    public ErrorsResponseDto updateFactOfTheDay(@ApiIgnore @AuthenticationPrincipal
                                                    Principal principal,
                                                @Valid @RequestBody FactOfTheDayPostDTO factOfTheDayPostDTO,
                                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ErrorsResponseDto errorsResponseDto = new ErrorsResponseDto();
            errorsResponseDto.setStatus(false);
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorsResponseDto.getErrors().add(
                    new FieldErrorDto(fieldError.getField(), fieldError.getDefaultMessage()));
            }
            return errorsResponseDto;
        }
        factOfTheDayService.updateFactOfTheDayAndTranslations(factOfTheDayPostDTO);
        return new ErrorsResponseDto(true, new ArrayList<>());
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
    @GetMapping("/delete")
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
    @ApiOperation(value = "Get all languages that exists in db")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PostMapping("/deleteAll")
    public ResponseEntity<List<Long>> deleteAll(@ApiIgnore @AuthenticationPrincipal
                                                    Principal principal, @RequestBody List<Long> listId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(factOfTheDayService.deleteAllFactOfTheDayAndTranslations(listId));
    }
}
