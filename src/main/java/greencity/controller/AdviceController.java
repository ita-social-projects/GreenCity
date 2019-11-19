package greencity.controller;

import static greencity.constant.ErrorMessage.INVALID_HABIT_ID;

import greencity.constant.HttpStatuses;
import greencity.dto.advice.AdviceAdminDTO;
import greencity.dto.advice.AdviceDto;
import greencity.dto.advice.AdvicePostDTO;
import greencity.entity.Advice;
import greencity.service.impl.AdviceServiceImpl;
import io.swagger.annotations.ApiOperation;
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
    private ModelMapper mapper;

    /**
     * The controller which returns random {@link Advice} by HabitDictionary id.
     *
     * @param habitId HabitDictionary
     * @return {@link AdviceDto}
     * @author Vitaliy Dzen
     */
    @ApiOperation("Get random advice by habit id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = INVALID_HABIT_ID)
    })
    @GetMapping("/random/{habitId}")
    public AdviceDto getRandomAdviceByHabitId(@PathVariable Long habitId) {
        return adviceService.getRandomAdviceByHabitId(habitId);
    }

    /**
     * The controller which returns all {@link Advice}.
     *
     * @return List of {@link AdviceDto}
     * @author Vitaliy Dzen
     */
    @ApiOperation("Get all advices")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK)
    })
    @GetMapping
    public List<AdviceAdminDTO> getAllAdvices() {
        return adviceService.getAllAdvices();
    }

    /**
     * The controller which save {@link Advice}.
     *
     * @param dto {@link AdviceDto}
     * @return {@link ResponseEntity}
     * @author Vitaliy Dzen
     */
    @ApiOperation(value = "Save advice")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping
    public ResponseEntity saveAdvice(@Valid @RequestBody AdvicePostDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adviceService.save(dto));
    }

    /**
     *  The controller which update {@link Advice}.
     *
     * @param dto {@link AdviceAdminDTO}
     * @return {@link ResponseEntity}
     * @author Vitaliy Dzen
     */
    @ApiOperation(value = "Update advice")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PutMapping("/{id}")
    public ResponseEntity<AdvicePostDTO> updateAdvice(
        @Valid @RequestBody AdvicePostDTO dto, @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(mapper.map(adviceService.update(dto, id), AdvicePostDTO.class));
    }

    /**
     * The controller which delete {@link Advice}.
     *
     * @param id of {@link Advice}
     * @return {@link ResponseEntity}
     * @author Vitaliy Dzen
     */
    @ApiOperation(value = "Delete advice")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        adviceService.delete(id);
        return ResponseEntity.ok().build();
    }
}
