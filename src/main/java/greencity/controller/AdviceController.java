package greencity.controller;

import static greencity.constant.ErrorMessage.INVALID_HABIT_ID;

import greencity.constant.AppConstant;
import greencity.constant.HttpStatuses;
import greencity.dto.advice.AdviceDTO;
import greencity.dto.advice.AdvicePostDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.Advice;
import greencity.entity.AdviceTranslation;
import greencity.service.impl.AdviceServiceImpl;
import greencity.service.impl.AdviceTranslationServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/advices")
@AllArgsConstructor
public class AdviceController {
    private AdviceServiceImpl adviceService;
    private AdviceTranslationServiceImpl adviceTranslationService;
    private ModelMapper mapper;

    /**
     * The controller which returns random {@link Advice} by HabitDictionary adviceId.
     *
     * @param habitId HabitDictionary
     * @return {@link AdviceDTO}
     * @author Vitaliy Dzen
     */
    @ApiOperation("Get random content by habit adviceId")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = INVALID_HABIT_ID),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/random/{habitId}")
    public LanguageTranslationDTO getRandomAdviceByHabitIdAndLanguage(
        @PathVariable Long habitId,
        @ApiParam(value = "Code of the needed language.", defaultValue = AppConstant.DEFAULT_LANGUAGE_CODE)
        @RequestParam(required = false, defaultValue = AppConstant.DEFAULT_LANGUAGE_CODE) String language) {
        return adviceService.getRandomAdviceByHabitIdAndLanguage(habitId, language);
    }

    /**
     * The controller which returns all {@link Advice}.
     *
     * @return List of {@link AdviceDTO}
     * @author Vitaliy Dzen
     */
    @ApiOperation("Get all advices")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping
    public List<LanguageTranslationDTO> getAll() {
        return adviceService.getAllAdvices();
    }

    /**
     * The controller which saveAdviceAndAdviceTranslation {@link Advice}.
     *
     * @param advice {@link AdviceDTO}
     * @return {@link ResponseEntity}
     * @author Vitaliy Dzen
     */
    @ApiOperation(value = "Save advice")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PostMapping
    public ResponseEntity<List<AdviceTranslation>> save(@Valid @RequestBody AdvicePostDTO advice) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(adviceTranslationService.saveAdviceAndAdviceTranslation(advice));
    }

    /**
     * The controller which update {@link Advice}.
     *
     * @param dto {@link AdviceDTO}
     * @return {@link ResponseEntity}
     * @author Vitaliy Dzen
     */
    @ApiOperation(value = "Update advice")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PutMapping("/{adviceId}")
    public ResponseEntity<AdvicePostDTO> update(
        @Valid @RequestBody AdvicePostDTO dto, @PathVariable Long adviceId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(mapper.map(adviceService.update(dto, adviceId), AdvicePostDTO.class));
    }

    /**
     * The controller which delete {@link Advice}.
     *
     * @param adviceId of {@link Advice}
     * @return {@link ResponseEntity}
     * @author Vitaliy Dzen
     */
    @ApiOperation(value = "Delete content")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @DeleteMapping("/{adviceId}")
    public ResponseEntity<Object> delete(@PathVariable Long adviceId) {
        adviceService.delete(adviceId);
        return ResponseEntity.ok().build();
    }
}
