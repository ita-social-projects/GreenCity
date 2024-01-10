package greencity.webcontroller;

import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.advice.AdvicePostDto;
import greencity.dto.advice.AdviceVO;
import greencity.dto.advice.AdviceViewDto;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.dto.habit.HabitVO;
import greencity.service.AdviceService;
import greencity.service.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
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
import java.util.Locale;
import static greencity.dto.genericresponse.GenericResponseDto.buildGenericResponseDto;

@Controller
@AllArgsConstructor
@RequestMapping("/management/advices")
public class ManagementAdvicesController {
    private final AdviceService adviceService;
    private final LanguageService languageService;

    /**
     * Method that returns management page with all {@link AdviceVO}'s that satisfy
     * query.
     *
     * @param filter   {@link String}
     * @param model    {@link Model} - for passing data between controller and view
     * @param pageable {@link Pageable}
     * @return name of template {@link String}
     * @author Markiyan Derevetskyi
     */
    @Operation(summary = "Get all advices")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @GetMapping
    public String findAllAdvices(@RequestParam(required = false) String filter,
        Model model, @Parameter(hidden = true) Pageable pageable) {
        PageableDto<AdviceVO> allAdvices = adviceService.getAllAdvicesWithFilter(pageable, filter);
        model.addAttribute("pageable", allAdvices);
        model.addAttribute("languages", languageService.getAllLanguages());

        return "core/management_advices";
    }

    /**
     * Method that finds {@link AdviceVO} by id.
     *
     * @param id {@link Long} - advice id
     * @return found advice {@link AdviceVO}
     * @author Markiyan Derevetskyi
     */
    @Operation(summary = "Get advice by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/{id}")
    public ResponseEntity<AdviceVO> findAdviceById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(adviceService.getAdviceById(id));
    }

    /**
     * Method that saves new {@link AdvicePostDto}.
     *
     * @param advice {@link AdvicePostDto} - advice that will be saved in DB.
     * @return saved advice {@link GenericResponseDto}
     * @author Markiyan Derevetskyi
     */
    @Operation(summary = "Save advice")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = GenericResponseDto.class))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @ResponseBody
    @PostMapping
    public GenericResponseDto saveAdvice(@Valid @RequestBody AdvicePostDto advice,
        BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            adviceService.save(advice);
        }

        return buildGenericResponseDto(bindingResult);
    }

    /**
     * Method that deletes advice by id.
     *
     * @param id {@link Long}
     * @return {@link org.springframework.http.ResponseEntity}
     * @author Markiyan Derevetskyi
     */
    @Operation(summary = "Delete advice by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteAdviceById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(adviceService.delete(id));
    }

    /**
     * Method that deletes all advices by given id's.
     *
     * @param ids - list of {@link Long}
     * @return {@link org.springframework.http.ResponseEntity}
     * @author Markiyan Derevetskyi
     */
    @Operation(summary = "Delete all advices by given id's")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
    })
    @DeleteMapping("/deleteAll")
    public ResponseEntity<List<Long>> deleteAllAdvices(@RequestBody List<Long> ids) {
        adviceService.deleteAllByIds(ids);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ids);
    }

    /**
     * Method that updates {@link AdvicePostDto} by id.
     *
     * @param advicePostDto {@link AdvicePostDto} - new advice
     * @param id            {@link Long} - advice id
     * @return updated advice {@link GenericResponseDto}
     * @author Markiyan Derevetskyi
     */
    @Operation(summary = "Update advice by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = GenericResponseDto.class))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
    })
    @ResponseBody
    @PutMapping("/{id}")
    public GenericResponseDto updateAdvice(@Valid @RequestBody AdvicePostDto advicePostDto, BindingResult bindingResult,
        @PathVariable Long id) {
        if (!bindingResult.hasErrors()) {
            adviceService.update(advicePostDto, id);
        }

        return buildGenericResponseDto(bindingResult);
    }

    /**
     * Method that returns management page with all {@link AdviceVO}'s that satisfy
     * filters.
     *
     * @param model    {@link Model} - for passing data between controller and view
     * @param pageable {@link Pageable}
     * @return name of template {@link String}
     * @author Markiyan Derevetskyi
     */
    @PostMapping("/filter")
    @Operation(summary = "Get all advices by filter data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    public String filterAdvices(Model model, @Parameter(hidden = true) Pageable pageable,
        AdviceViewDto adviceViewDto) {
        PageableDto<AdviceVO> filteredAdvices =
            adviceService.getFilteredAdvices(pageable, adviceViewDto);
        model.addAttribute("pageable", filteredAdvices);
        model.addAttribute("languages", languageService.getAllLanguages());
        model.addAttribute("fields", adviceViewDto);

        return "core/management_advices";
    }

    /**
     * Deletes Advices by indexes in list.
     *
     * @param habitId of {@link HabitVO}.
     * @return {@link HttpStatus}.
     * @author Vira Maksymets
     */
    @Operation(summary = "Deletes Advices by ids in list.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @DeleteMapping("/{habitId}/unlink/advice")
    public ResponseEntity<Long> unlinkAdvice(@PathVariable("habitId") Long habitId,
        @Parameter(hidden = true) Locale locale,
        @RequestBody Integer[] advicesIndexes) {
        adviceService.unlinkAdvice(locale.getLanguage(), habitId, advicesIndexes);
        return ResponseEntity.status(HttpStatus.OK).body(habitId);
    }
}
