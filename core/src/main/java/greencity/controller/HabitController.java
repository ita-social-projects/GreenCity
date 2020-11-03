package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ApiPageableWithLocale;
import greencity.annotations.CurrentUser;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitAssignStatDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.dto.user.UserVO;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import greencity.service.HabitAssignService;
import greencity.service.HabitService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.Locale;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/habit")
public class HabitController {
    private final HabitAssignService habitAssignService;
    private final HabitService habitService;

    /**
     * Method finds {@link Habit} by given id with locale translation.
     *
     * @param id     of {@link Habit}.
     * @param locale {@link Locale} with needed language code.
     * @return {@link HabitDto}.
     */
    @ApiOperation(value = "Find habit by id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND),
    })
    @GetMapping("/{id}")
    @ApiLocale
    public ResponseEntity<HabitDto> getHabitById(@PathVariable Long id,
                                                 @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitService.getByIdAndLanguageCode(id, locale.getLanguage()));
    }

    /**
     * Method finds all habits that available for tracking for specific language.
     *
     * @param locale   {@link Locale} with needed language code.
     * @param pageable {@link Pageable} instance.
     * @return Pageable of {@link HabitTranslationDto}.
     */
    @ApiOperation(value = "Find all habits.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
    })
    @GetMapping("")
    @ApiPageableWithLocale
    public ResponseEntity<PageableDto<HabitDto>> getAll(
        @ApiIgnore @ValidLanguage Locale locale,
        @ApiIgnore Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(
            habitService.getAllHabitsByLanguageCode(pageable, locale.getLanguage()));
    }

    /**
     * Method which assigns habit for {@link User}.
     *
     * @param id     {@link Habit} id.
     * @param userVO {@link UserVO} instance.
     * @return {@link ResponseEntity}.
     */
    @ApiOperation(value = "Assign habit for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/assign/{id}")
    public ResponseEntity<HabitAssignDto> assign(@PathVariable Long id,
                                                 @ApiIgnore @CurrentUser UserVO userVO) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(habitAssignService.assignHabitForUser(id, userVO));
    }

    /**
     * Method returns {@link HabitAssign} by it's id.
     *
     * @param id {@link HabitAssign} id.
     * @return {@link HabitAssignDto}.
     */
    @ApiOperation(value = "Get habit assign.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/assign/{id}")
    public ResponseEntity<HabitAssignDto> getHabitAssign(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService.getById(id));
    }

    /**
     * Method to return all {@link HabitAssign} by it's {@link Habit} id.
     *
     * @param id       {@link Habit} id.
     * @param acquired {@link Boolean} status.
     * @return {@link List} of {@link HabitAssignDto}.
     */
    @ApiOperation(value = "Get all users assigns from certain habit.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = List.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{id}/assign/all")
    public ResponseEntity<List<HabitAssignDto>> getAllHabitAssignsByHabitIdAndAcquired(@PathVariable Long id,
                                                                                       @RequestParam Boolean acquired) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService.getAllHabitAssignsByHabitIdAndAcquiredStatus(id, acquired));
    }

    /**
     * Method to return {@link HabitAssign} by it's {@link Habit} id.
     *
     * @param id     {@link Habit} id.
     * @param userVO {@link UserVO} user.
     * @return {@link HabitAssignDto} instance.
     */
    @ApiOperation(value = "Get active assign by habit id for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{id}/assign")
    public ResponseEntity<HabitAssignDto> getHabitAssignByHabitId(
        @ApiIgnore @CurrentUser UserVO userVO,
        @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService.findActiveHabitAssignByUserIdAndHabitId(userVO.getId(), id));
    }

    /**
     * Method to update active {@link HabitAssign} for it's {@link Habit} id and current user.
     *
     * @param userVO             {@link UserVO} instance.
     * @param id                 {@link Habit} id.
     * @param habitAssignStatDto {@link HabitAssignStatDto} instance.
     * @return {@link HabitAssignDto}.
     */
    @ApiOperation(value = "Update active user habit assign acquired or suspended status.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PatchMapping("/{id}/assign")
    public ResponseEntity<HabitAssignDto> updateAssignByHabitId(
        @ApiIgnore @CurrentUser UserVO userVO,
        @PathVariable Long id, @Valid @RequestBody HabitAssignStatDto habitAssignStatDto) {
        return ResponseEntity.status(HttpStatus.OK).body(habitAssignService
            .updateStatusByHabitIdAndUserId(id, userVO.getId(), habitAssignStatDto));
    }
}
