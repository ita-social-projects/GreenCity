package greencity.webcontroller;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.dto.goal.*;
import greencity.dto.habitfact.HabitFactTranslationVO;
import greencity.dto.habitfact.HabitFactVO;
import greencity.service.GoalService;
import greencity.service.LanguageService;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import static greencity.dto.genericresponse.GenericResponseDto.buildGenericResponseDto;

@Controller
@AllArgsConstructor
@RequestMapping("/management/goals")
public class ManagementGoalsController {
    private final GoalService goalService;
    private final LanguageService languageService;

    /**
     * Method that returns management page with all {@link GoalVO}.
     *
     * @param query    Query for searching related data
     * @param model    Model that will be configured and returned to user.
     * @param pageable {@link Pageable}.
     * @return View template path {@link String}.
     */
    @GetMapping
    public String getAllUsers(@RequestParam(required = false, name = "query") String query, Pageable pageable,
                              Model model) {
        Pageable paging = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id").ascending());
        PageableAdvancedDto<GoalManagementDto> pageableDto = query == null || query.isEmpty()
            ? goalService.findGoalForManagementByPage(paging)
            : goalService.searchBy(paging, query);
        model.addAttribute("goals", pageableDto);
        model.addAttribute("languages", languageService.getAllLanguages());
        return "core/management_goals";
    }

    /**
     * The controller which saveGoal {@link GoalVO}.
     *
     * @param goalPostDto {@link GoalPostDto}
     * @return {@link ResponseEntity}
     * @author Dmytro Khonko
     */
    @PostMapping
    @ResponseBody
    public GenericResponseDto save(@Valid @RequestBody  GoalPostDto goalPostDto,
                                   BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            goalService.saveGoal(goalPostDto);
        }
        return GenericResponseDto.buildGenericResponseDto(bindingResult);
    }

    /**
     * The controller which update {@link GoalTranslationVO}.
     *
     * @param goalPostDto {@link GoalPostDto}
     * @return {@link ResponseEntity}
     * @author Dmytro Khonko
     */
    @PutMapping("/{id}")
    @ResponseBody
    public GenericResponseDto update(
        @Valid @RequestBody GoalPostDto goalPostDto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            goalService.update(goalPostDto);
        }
        return buildGenericResponseDto(bindingResult);
    }

    /**
     * Method for goals by  id.
     *
     * @return {@link GoalVO} instance.
     * @author Dmytro Khonko
     */
    @GetMapping("/find/{id}")
    public ResponseEntity<GoalResponseDto> getHabitFactsById(
            @PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(goalService.findGoalById(id));
    }

    /**
     * The controller which delete {@link GoalVO}.
     *
     * @param id of {@link GoalVO}
     * @return {@link ResponseEntity}
     * @author Dmytro Khonko
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(goalService.delete(id));
    }
    /**
     * Method which deletes {@link HabitFactVO} and {@link HabitFactTranslationVO}
     * by given id.
     *
     * @param listId list of IDs
     * @return {@link ResponseEntity}
     * @author Dmytro Khonko
     */
    @DeleteMapping("/deleteAll")
    @ResponseBody
    public ResponseEntity<List<Long>> deleteAll(@RequestBody List<Long> listId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(goalService.deleteAllGoalByListOfId(listId));
    }

}
