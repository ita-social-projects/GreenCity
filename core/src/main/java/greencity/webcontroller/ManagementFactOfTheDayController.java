package greencity.webcontroller;

import greencity.annotations.ApiPageable;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.factoftheday.FactOfTheDayDTO;
import greencity.dto.factoftheday.FactOfTheDayPostDTO;
import greencity.dto.genericresponse.FieldErrorDto;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.entity.FactOfTheDay;
import greencity.service.FactOfTheDayService;
import greencity.service.LanguageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@AllArgsConstructor
@RequestMapping("/management/factoftheday")
public class ManagementFactOfTheDayController {
    @Autowired
    private FactOfTheDayService factOfTheDayService;
    @Autowired
    private LanguageService languageService;

    /**
     * Returns management page with all facts of the day.
     *
     * @param model ModelAndView that will be configured and returned to user
     * @return model
     */
    @ApiPageable
    @ApiOperation(value = "Get management page with facts of the day.")
    @GetMapping("")
    public String getAllFacts(Model model, @ApiIgnore Pageable pageable) {
        PageableDto<FactOfTheDayDTO> allFactsOfTheDay = factOfTheDayService.getAllFactsOfTheDay(pageable);
        model.addAttribute("pageable", allFactsOfTheDay);
        model.addAttribute("languages", languageService.getAllLanguages());
        return "core/management_fact_of_the_day";
    }

    /**
     * Returns management page with facts of the day that satisfy query.
     *
     * @param query string to search
     * @return model
     */
    @ApiPageable
    @ApiOperation(value = "Get management page with facts of the day that satisfy query.")
    @GetMapping("/findAll")
    public String findAll(@RequestParam(required = false, name = "query") String query,
                          Model model, @ApiIgnore Pageable pageable) {
        PageableDto<FactOfTheDayDTO> pageableDto = query == null || query.isEmpty()
            ? factOfTheDayService.getAllFactsOfTheDay(pageable) : factOfTheDayService.searchBy(pageable, query);
        model.addAttribute("pageable", pageableDto);
        model.addAttribute("languages", languageService.getAllLanguages());
        return "core/management_fact_of_the_day";
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
        return GenericResponseDto.builder().build();
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
    public GenericResponseDto updateFactOfTheDay(@Valid @RequestBody FactOfTheDayPostDTO factOfTheDayPostDTO,
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
        return GenericResponseDto.builder().build();
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
    public ResponseEntity<Long> delete(@RequestParam("id") Long id) {
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
    public ResponseEntity<List<Long>> deleteAll(@RequestBody List<Long> listId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(factOfTheDayService.deleteAllFactOfTheDayAndTranslations(listId));
    }
}
