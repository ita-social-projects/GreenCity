package greencity.webcontroller;

import static greencity.dto.genericresponse.GenericResponseDto.buildGenericResponseDto;

import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactVO;
import greencity.entity.HabitFact;
import greencity.entity.HabitFactTranslation;
import greencity.service.HabitFactService;
import greencity.service.HabitFactTranslationService;
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
@RequestMapping("/management/facts")
public class ManagementHabitFactsController {
    @Autowired
    private HabitFactService habitFactService;
    @Autowired
    private HabitFactTranslationService habitFactTranslationService;
    @Autowired
    private LanguageService languageService;

    /**
     * Returns management page with all {@link HabitFact}'s.
     *
     * @param model Model that will be configured.
     * @return View template path {@link String}.
     * @author Ivan Behar
     */
    @ApiOperation(value = "Get management page with habit facts.")
    @GetMapping
    public String findAll(Model model, @ApiIgnore Pageable pageable) {
        PageableDto<HabitFactVO> pageableDto = habitFactService.getAllHabitFactsVO(pageable);
        model.addAttribute("pageable", pageableDto);
        model.addAttribute("languages", languageService.getAllLanguages());
        return "core/management_habit_facts";
    }

    /**
     * Method which saves {@link HabitFact}.
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
    @PostMapping("/")
    public GenericResponseDto saveHabitFacts(@Valid @RequestBody HabitFactPostDto habitFactPostDto,
                                             BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            habitFactTranslationService.saveHabitFactAndFactTranslation(habitFactPostDto);
        }
        return buildGenericResponseDto(bindingResult);
    }

    /**
     * Method which updates {@link HabitFact}.
     *
     * @param habitFactPostDto of {@link HabitFactPostDto}
     * @return {@link GenericResponseDto} with of operation and errors fields
     * @author Ivan Behar
     */
    @ApiOperation(value = "Update fact of the day")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @ResponseBody
    @PutMapping("/")
    public GenericResponseDto updateFactOfTheDay(@Valid @RequestBody HabitFactPostDto habitFactPostDto,
                                                 BindingResult bindingResult,
                                                 Long id) {
        if (!bindingResult.hasErrors()) {
            habitFactService.update(habitFactPostDto, id);
        }
        return buildGenericResponseDto(bindingResult);
    }

    /**
     * Method which deletes {@link HabitFact} and {@link HabitFactTranslation} by given id.
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
    @DeleteMapping("/")
    public ResponseEntity<Long> delete(@RequestParam("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitFactService.delete(id));
    }

    /**
     * Method which deletes {@link HabitFact} and {@link HabitFactTranslation} by given id.
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
}
