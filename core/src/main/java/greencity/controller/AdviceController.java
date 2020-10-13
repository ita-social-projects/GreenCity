package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.advice.AdviceDto;
import greencity.dto.advice.AdvicePostDto;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.Advice;
import greencity.entity.localization.AdviceTranslation;
import greencity.service.AdviceService;
import greencity.service.AdviceTranslationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.Locale;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import static greencity.constant.ErrorMessage.INVALID_HABIT_ID;

@RestController
@RequestMapping("/advices")
@AllArgsConstructor
public class AdviceController {
    private final AdviceService adviceService;
    private final AdviceTranslationService adviceTranslationService;
    private final ModelMapper mapper;

    /**
     * The controller which returns random {@link Advice} by HabitDictionary adviceId.
     *
     * @param habitId HabitDictionary
     * @return {@link AdviceDto}
     * @author Vitaliy Dzen
     */
    @ApiOperation("Get random content by habit adviceId")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = INVALID_HABIT_ID),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/random/{habitId}")
    @ApiLocale
    public LanguageTranslationDTO getRandomAdviceByHabitIdAndLanguage(
        @PathVariable Long habitId,
        @ApiIgnore @ValidLanguage Locale locale) {
        return adviceService.getRandomAdviceByHabitIdAndLanguage(habitId, locale.getLanguage());
    }

    /**
     * The controller which returns all {@link Advice}.
     *
     * @return List of {@link AdviceDto}
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
     * @param advice {@link AdviceDto}
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
    public ResponseEntity<List<AdviceTranslation>> save(@Valid @RequestBody AdvicePostDto advice) {
        List<AdviceTranslation> response = mapper.map(adviceTranslationService.saveAdviceAndAdviceTranslation(advice),
            new TypeToken<List<AdviceTranslation>>() {
            }.getType());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * The controller which update {@link Advice}.
     *
     * @param dto {@link AdviceDto}
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
    public ResponseEntity<AdvicePostDto> update(
        @Valid @RequestBody AdvicePostDto dto, @PathVariable Long adviceId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(mapper.map(adviceService.update(dto, adviceId), AdvicePostDto.class));
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
