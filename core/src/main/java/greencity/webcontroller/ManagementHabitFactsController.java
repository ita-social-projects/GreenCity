package greencity.webcontroller;

import static greencity.dto.genericresponse.GenericResponseDto.buildGenericResponseDto;

import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.dto.habitfact.*;
import greencity.service.HabitFactService;
import greencity.service.LanguageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/management/facts")
public class ManagementHabitFactsController {
    @Autowired
    private HabitFactService habitFactService;
    @Autowired
    private LanguageService languageService;

    /**
     * Method for getting habit facts by id.
     *
     * @return {@link HabitFactVO} instance.
     * @author Ivan Behar
     */
    @ApiOperation(value = "Get habit facts by id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitFactDtoResponse.class),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/find/{id}")
    public ResponseEntity<HabitFactDtoResponse> getHabitFactsById(
        @PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(habitFactService.getHabitFactById(id));
    }

    /**
     * Returns management page with all {@link HabitFactVO}'s.
     *
     * @param model Model that will be configured.
     * @return View template path {@link String}.
     * @author Ivan Behar
     */
    @ApiOperation(value = "Get management page with habit facts.")
    @GetMapping
    public String findAll(@RequestParam(required = false, name = "query") String filter,
        Model model, @ApiIgnore Pageable pageable) {
        model.addAttribute("pageable", habitFactService.getAllHabitFactVOsWithFilter(filter, pageable));
        model.addAttribute("languages", languageService.getAllLanguages());
        return "core/management_habit_facts";
    }

    /**
     * Method which saves {@link HabitFactVO}.
     *
     * @param habitFactPostDto of {@link HabitFactPostDto}
     * @return {@link GenericResponseDto} with of operation and errors fields
     * @author Ivan Behar
     */
    @ApiOperation(value = "Save habit facts")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = GenericResponseDto.class),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @ResponseBody
    @PostMapping
    public GenericResponseDto saveHabitFacts(@Valid @RequestBody HabitFactPostDto habitFactPostDto,
        BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            habitFactService.save(habitFactPostDto);
        }
        return buildGenericResponseDto(bindingResult);
    }

    /**
     * Method which updates {@link HabitFactVO}.
     *
     * @param habitFactUpdateDto of {@link HabitFactPostDto}
     * @return {@link GenericResponseDto} with of operation and errors fields
     * @author Ivan Behar
     */
    @ApiOperation(value = "Update fact of the day")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @ResponseBody
    @PutMapping("/{id}")
    public GenericResponseDto updateHabitFacts(@Valid @RequestBody HabitFactUpdateDto habitFactUpdateDto,
        BindingResult bindingResult,
        @PathVariable Long id) {
        if (!bindingResult.hasErrors()) {
            habitFactService.update(habitFactUpdateDto, id);
        }
        return buildGenericResponseDto(bindingResult);
    }

    /**
     * Method which deletes {@link HabitFactVO} and {@link HabitFactTranslationVO}
     * by given id.
     *
     * @param id of Fact of the day
     * @return {@link ResponseEntity}
     * @author Ivan Behar
     */
    @ApiOperation(value = "Delete Habit Facts")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> delete(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitFactService.delete(id));
    }

    /**
     * Method which deletes {@link HabitFactVO} and {@link HabitFactTranslationVO}
     * by given id.
     *
     * @param listId list of IDs
     * @return {@link ResponseEntity}
     * @author Ivan Behar
     */
    @ApiOperation(value = "Delete all Habit Facts by given IDs")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @DeleteMapping("/deleteAll")
    public ResponseEntity<List<Long>> deleteAll(@RequestBody List<Long> listId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitFactService.deleteAllHabitFactsByListOfId(listId));
    }

    /**
     * Returns management page with Habit facts filtered data.
     *
     * @param model            ModelAndView that will be configured and returned to
     *                         user.
     * @param habitFactViewDto used for receive parameters for filters from UI.
     */
    @ApiOperation(value = "Get all habit facts by filter data")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping(value = "/filter")
    public String filterData(Model model,
        @ApiIgnore Pageable pageable,
        HabitFactViewDto habitFactViewDto) {
        PageableDto<HabitFactVO> pageableDto =
            habitFactService.getFilteredDataForManagementByPage(
                pageable,
                habitFactViewDto);
        model.addAttribute("pageable", pageableDto);
        model.addAttribute("languages", languageService.getAllLanguages());
        model.addAttribute("fields", habitFactViewDto);
        return "core/management_habit_facts";
    }
}
