package greencity.webcontroller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ImageValidation;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.dto.goal.*;
import greencity.dto.habit.HabitManagementDto;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserVO;
import greencity.service.GoalService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.Locale;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@AllArgsConstructor
@RequestMapping("/management/goals")
public class ManagementGoalsController {
    private final GoalService goalService;
    private final ModelMapper mapper;

    /**
     * Method that returns management page with all {@link UserVO}.
     *
     * @param query    Query for searching related data
     * @param model    Model that will be configured and returned to user.
     * @param pageable {@link Pageable}.
     * @return View template path {@link String}.
     * @author Vasyl Zhovnir
     */
    @GetMapping
    public String getAllUsers(@RequestParam(required = false, name = "query") String query, Pageable pageable,
                              Model model) {
        Pageable paging = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id").descending());
        PageableAdvancedDto<GoalManagementDto> pageableDto = query == null || query.isEmpty()
            ? goalService.findGoalForManagementByPage(paging)
            : goalService.searchBy(paging, query);
        model.addAttribute("goals", pageableDto);
        return "core/management_goals";
    }

    /**
     * The controller which saveGoal {@link GoalVO}.
     *
     * @param goalPostDto {@link GoalDto}
     * @return {@link ResponseEntity}
     */
    @ApiOperation(value = "Save goal")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PostMapping("/save")
    public GenericResponseDto save(@Valid @RequestPart GoalManagementDto goalManagementDto,
                                   BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            goalService.saveGoal(goalManagementDto);
        }
        return GenericResponseDto.buildGenericResponseDto(bindingResult);
    }

    /**
     * The controller which update {@link GoalTranslationVO}.
     *
     * @param goalPostDto {@link GoalPostDto}
     * @return {@link ResponseEntity}
     */
    @ApiOperation(value = "Update goal")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PutMapping("/{id}")
    public ResponseEntity<List<LanguageTranslationDTO>> update(
        @Valid @RequestBody GoalPostDto goalPostDto) {
//        return ResponseEntity.status(HttpStatus.OK).body(goalService.update(goalPostDto));
        return null;
    }

    /**
     * The controller which delete {@link GoalVO}.
     *
     * @param id of {@link GoalVO}
     * @return {@link ResponseEntity}
     */
    @ApiOperation(value = "Delete goal")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        goalService.delete(id);
        return ResponseEntity.ok().build();
    }
}
