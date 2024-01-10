package greencity.webcontroller;

import static greencity.dto.genericresponse.GenericResponseDto.buildGenericResponseDto;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.dto.habitfact.*;
import greencity.service.HabitFactService;
import greencity.service.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PutMapping;
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
    @Operation(summary = "Get habit facts by id.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = HabitFactDtoResponse.class))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
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
    @Operation(summary = "Get management page with habit facts.")
    @GetMapping
    public String findAll(@RequestParam(required = false, name = "query") String filter,
        Model model, @Parameter(hidden = true) Pageable pageable) {
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
    @Operation(summary = "Save habit facts")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = GenericResponseDto.class))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
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
    @Operation(summary = "Update fact of the day")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
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
    @Operation(summary = "Delete Habit Facts")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
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
    @Operation(summary = "Delete all Habit Facts by given IDs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
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
    @Operation(summary = "Get all habit facts by filter data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @PostMapping(value = "/filter")
    public String filterData(Model model,
        @Parameter(hidden = true) Pageable pageable,
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
