package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.advice.AdviceDto;
import greencity.dto.advice.AdvicePostDto;
import greencity.dto.advice.AdviceVO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.service.AdviceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.Locale;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@Validated
public class AdviceController {
    private final AdviceService adviceService;
    private final ModelMapper mapper;

    /**
     * The controller which returns random {@link AdviceVO} by HabitDictionary
     * adviceId.
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
     * The controller which returns all {@link AdviceVO}.
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
     * The controller which returns advice by id {@link AdviceVO}.
     *
     * @return instance of {@link AdviceVO}
     * @author Markiyan Derevetskyi
     */
    @ApiOperation("Get advice by id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/{id}")
    public AdviceVO getById(@PathVariable Long id) {
        return adviceService.getAdviceById(id);
    }

    /**
     * The controller which saveAdviceAndAdviceTranslation {@link AdviceVO}.
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
    public ResponseEntity<AdviceVO> save(@Valid @RequestBody AdvicePostDto advice) {
        AdviceVO response = adviceService.save(advice);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * The controller which update {@link AdviceVO}.
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
     * The controller which delete {@link AdviceVO}.
     *
     * @param adviceId of {@link AdviceVO}
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
