package greencity.webcontroller;

import static greencity.dto.genericresponse.GenericResponseDto.buildGenericResponseDto;

import greencity.annotations.ApiPageable;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.factoftheday.FactOfTheDayDTO;
import greencity.dto.factoftheday.FactOfTheDayPostDTO;
import greencity.dto.factoftheday.FactOfTheDayTranslationVO;
import greencity.dto.factoftheday.FactOfTheDayVO;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.service.FactOfTheDayService;
import greencity.service.LanguageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import javax.validation.Valid;
import java.util.List;

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
            ? factOfTheDayService.getAllFactsOfTheDay(pageable)
            : factOfTheDayService.searchBy(pageable, query);
        model.addAttribute("pageable", pageableDto);
        model.addAttribute("languages", languageService.getAllLanguages());
        return "core/management_fact_of_the_day";
    }

    /**
     * Method which saves {@link FactOfTheDayVO}.
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
        if (!bindingResult.hasErrors()) {
            factOfTheDayService.saveFactOfTheDayAndTranslations(factOfTheDayPostDTO);
        }
        return buildGenericResponseDto(bindingResult);
    }

    /**
     * Method which updates {@link FactOfTheDayVO}.
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
        if (!bindingResult.hasErrors()) {
            factOfTheDayService.updateFactOfTheDayAndTranslations(factOfTheDayPostDTO);
        }
        return buildGenericResponseDto(bindingResult);
    }

    /**
     * Method which deteles {@link FactOfTheDayVO} and
     * {@link FactOfTheDayTranslationVO} by given id.
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
     * Method which deteles {@link FactOfTheDayVO} and
     * {@link FactOfTheDayTranslationVO} by given id.
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
