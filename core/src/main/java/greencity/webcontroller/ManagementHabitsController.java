package greencity.webcontroller;

import greencity.annotations.ImageValidation;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.entity.Habit;
import greencity.entity.HabitFact;
import greencity.entity.HabitFactTranslation;
import greencity.service.HabitService;
import greencity.service.LanguageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@AllArgsConstructor
@RequestMapping("/management/habits")
public class ManagementHabitsController {
    private final HabitService habitService;
    private final LanguageService languageService;

    /**
     * Returns management page with all habits.
     *
     * @param model ModelAndView that will be configured and returned to user
     * @return model
     * @author Dovganyuk Taras
     */

    @GetMapping
    public String getAllHabits(Model model, @ApiIgnore Pageable pageable) {
        PageableDto<HabitDto> allHabits = habitService.getAllHabitsDto(pageable);
        model.addAttribute("pageable", allHabits);
        model.addAttribute("languages", languageService.getAllLanguages());
        return "core/management_user_habits";
    }

    /**
     * Method which return {@link HabitDto} by given id.
     *
     * @param id of {@link Habit}
     * @return {@link HabitDto}
     */
    @ApiOperation(value = "Find habit by given id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitDto.class),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/find")
    public ResponseEntity<HabitDto> findHabit(@ApiIgnore @AuthenticationPrincipal
                                                  Principal principal, @RequestParam("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitService.getById(id));
    }

    /**
     * Method which saves {@link Habit} with translations.
     *
     * @param habitDto {@link HabitDto}.
     * @return {@link HabitDto}.
     */
    @ApiOperation(value = "Save habit with translations.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })

    @ResponseBody
    @PutMapping("/update")
    public GenericResponseDto update(@Valid @RequestPart HabitDto habitDto,
                                   BindingResult bindingResult,
                                   @ImageValidation
                                   @RequestParam(required = false, name = "file") MultipartFile file) {
        System.out.println("\n\n\n\n\n\n\n\n\nerror!!!");
        if (!bindingResult.hasErrors()) {
            System.out.println("\n\n\n\n\n\n\n\n\nNo ERROR!!!");
            habitService.update(habitDto, file);
        }
        return GenericResponseDto.buildGenericResponseDto(bindingResult);
    }

    /**
     * Method which updates {@link Habit} with translations.
     *
     * @param habitDto {@link HabitDto}.
     * @return {@link HabitDto}.
     */
    @ApiOperation(value = "Save habit with translations.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @ResponseBody
    @PostMapping("/save")
    public GenericResponseDto save(@Valid @RequestPart HabitDto habitDto,
                                   BindingResult bindingResult,
                                   @ImageValidation
                                   @RequestParam(required = false, name = "file") MultipartFile file) {
        System.out.println("\n\n\n\n\n\n\n\n\nerror!!!");
        if (!bindingResult.hasErrors()) {
            System.out.println("\n\n\n\n\n\n\n\n\nNo ERROR!!!");
            habitService.saveHabitAndTranslations(habitDto, file);
        }
        return GenericResponseDto.buildGenericResponseDto(bindingResult);
    }
}
