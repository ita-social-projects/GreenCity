package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.goal.GoalDto;
import greencity.dto.goal.GoalPostDto;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.goal.GoalTranslationVO;
import greencity.dto.goal.GoalVO;
import greencity.service.GoalService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.Locale;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/goals")
public class GoalController {
//    private final GoalService goalService;
//    private final ModelMapper mapper;
//
//    /**
//     * Constructor with parameters.
//     */
//
//    @Autowired
//    public GoalController(GoalService goalService, ModelMapper mapper) {
//        this.goalService = goalService;
//        this.mapper = mapper;
//    }
//
//    /**
//     * Method returns all goals, available for tracking for specific language.
//     *
//     * @param locale needed language code
//     * @return list of {@link GoalDto}
//     */
//    @ApiOperation(value = "Get all goals.")
//    @ApiResponses(value = {
//        @ApiResponse(code = 200, message = HttpStatuses.OK),
//        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
//        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
//    })
//    @GetMapping("")
//    @ApiLocale
//    public ResponseEntity<List<GoalDto>> getAll(
//        @ApiIgnore @ValidLanguage Locale locale) {
//        return ResponseEntity.status(HttpStatus.OK).body(goalService.findAll(locale.getLanguage()));
//    }
//
//    /**
//     * The controller which saveGoal {@link GoalVO}.
//     *
//     * @param goalPostDto {@link GoalDto}
//     * @return {@link ResponseEntity}
//     */
//    @ApiOperation(value = "Save goal")
//    @ApiResponses(value = {
//        @ApiResponse(code = 200, message = HttpStatuses.OK),
//        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
//        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
//    })
//    @PostMapping
//    public ResponseEntity<List<LanguageTranslationDTO>> save(@Valid @RequestBody GoalPostDto goalPostDto) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(goalService.saveGoal(goalPostDto));
//    }
//
//    /**
//     * The controller which update {@link GoalTranslationVO}.
//     *
//     * @param goalPostDto {@link GoalPostDto}
//     * @return {@link ResponseEntity}
//     */
//    @ApiOperation(value = "Update goal")
//    @ApiResponses(value = {
//        @ApiResponse(code = 200, message = HttpStatuses.OK),
//        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
//        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
//    })
//    @PutMapping("/{id}")
//    public ResponseEntity<List<LanguageTranslationDTO>> update(
//        @Valid @RequestBody GoalPostDto goalPostDto) {
//        return ResponseEntity.status(HttpStatus.OK).body(goalService.update(goalPostDto));
//    }
//
//    /**
//     * The controller which delete {@link GoalVO}.
//     *
//     * @param id of {@link GoalVO}
//     * @return {@link ResponseEntity}
//     */
//    @ApiOperation(value = "Delete goal")
//    @ApiResponses(value = {
//        @ApiResponse(code = 200, message = HttpStatuses.OK),
//        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
//        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
//    })
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Object> delete(@PathVariable Long id) {
//        goalService.delete(id);
//        return ResponseEntity.ok().build();
//    }
}
