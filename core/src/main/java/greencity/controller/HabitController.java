package greencity.controller;

import greencity.constant.AppConstant;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.HabitDictionaryTranslationsDto;
import greencity.entity.Habit;
import greencity.entity.User;
import greencity.service.HabitService;
import greencity.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/habit")
public class HabitController {
    private HabitService habitService;
    private UserService userService;

    /**
     * Method which assign habit for {@link User}.
     *
     * @param habitId - id of {@link Habit}
     * @return {@link ResponseEntity}
     */
    @ApiOperation(value = "Assign habit.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = AddHabitStatisticDto.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/assign/{habitId}")
    public ResponseEntity<Object> assign(@PathVariable Long habitId, @ApiIgnore @AuthenticationPrincipal
        Principal principal) {
        User user = userService.findByEmail(principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(habitService.assignHabitForUser(habitId, user));
    }

    /**
     * Method returns all habits, available for tracking for specific language.
     *
     * @param language needed language code
     * @return Pageable of {@link greencity.dto.habitstatistic.HabitDictionaryTranslationsDto}
     */
    @ApiOperation(value = "Get all habits.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("")
    public ResponseEntity<PageableDto<HabitDictionaryTranslationsDto>> getAll(
        @ApiIgnore Pageable pageable,
        @ApiParam(value = "Code of the needed language.", defaultValue = AppConstant.DEFAULT_LANGUAGE_CODE)
        @RequestParam(required = false, defaultValue = AppConstant.DEFAULT_LANGUAGE_CODE) String language) {
        return ResponseEntity.status(HttpStatus.OK).body(habitService.getAllHabitsByLanguageCode(pageable, language));
    }
}
