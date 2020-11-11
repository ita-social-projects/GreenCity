package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.CurrentUser;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.habit.*;
import greencity.dto.user.UserVO;
import greencity.service.HabitAssignService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.Locale;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/habit")
public class HabitAssignController {
    private final HabitAssignService habitAssignService;

    /**
     * Method which assigns habit for {@link UserVO} with default props.
     *
     * @param id     {@link HabitVO} id.
     * @param userVO {@link UserVO} instance.
     * @return {@link ResponseEntity}.
     */
    @ApiOperation(value = "Assign habit with default properties for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/assign/{id}")
    public ResponseEntity<HabitAssignDto> assignDefault(@PathVariable Long id,
                                                        @ApiIgnore @CurrentUser UserVO userVO) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(habitAssignService.assignDefaultHabitForUser(id, userVO));
    }

    /**
     * Method which assigns habit for {@link UserVO} with custom props.
     *
     * @param id                       {@link HabitVO} id.
     * @param userVO                   {@link UserVO} instance.
     * @param habitAssignPropertiesDto {@link HabitAssignPropertiesDto} instance.
     * @return {@link ResponseEntity}.
     */
    @ApiOperation(value = "Assign habit with custom properties for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/assign/{id}/custom")
    public ResponseEntity<HabitAssignDto> assignCustom(@PathVariable Long id,
                                                       @ApiIgnore @CurrentUser UserVO userVO,
                                                       @Valid @RequestBody
                                                           HabitAssignPropertiesDto habitAssignPropertiesDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(habitAssignService.assignCustomHabitForUser(id, userVO, habitAssignPropertiesDto));
    }

    /**
     * Method returns {@link HabitAssignDto} by it's id.
     *
     * @param id {@link HabitAssignVO} id.
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
     * Method for finding all active {@link HabitAssignDto}'s for current user.
     *
     * @param userVO   {@link UserVO} instance.
     * @param acquired {@link Boolean} status.
     * @param locale   needed language code.
     * @return list of {@link HabitAssignDto}.
     */
    @ApiOperation(value = "Get active habit assigns for current user by acquired status.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = List.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ApiLocale
    @GetMapping("/assign")
    public ResponseEntity<List<HabitAssignDto>> getCurrentUserHabitAssignsByIdAndAcquired(
        @ApiIgnore @CurrentUser UserVO userVO, @RequestParam Boolean acquired,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService
                .getAllHabitAssignsByUserIdAndAcquiredStatus(userVO.getId(), acquired, locale.getLanguage()));
    }

    /**
     * Method to return all {@link HabitAssignVO} by it's {@link HabitVO} id.
     *
     * @param id       {@link HabitVO} id.
     * @param acquired {@link Boolean} status.
     * @param locale   needed language code.
     * @return {@link List} of {@link HabitAssignDto}.
     */
    @ApiOperation(value = "Get all active users assigns from certain habit.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = List.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ApiLocale
    @GetMapping("/{id}/assign/all")
    public ResponseEntity<List<HabitAssignDto>> getAllHabitAssignsByHabitIdAndAcquired(@PathVariable Long id,
                                                                                       @RequestParam Boolean acquired,
                                                                                       @ApiIgnore @ValidLanguage
                                                                                           Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService.getAllHabitAssignsByHabitIdAndAcquiredStatus(id, acquired, locale.getLanguage()));
    }

    /**
     * Method to return {@link HabitAssignVO} by it's {@link HabitVO} id.
     *
     * @param id     {@link HabitVO} id.
     * @param userVO {@link UserVO} user.
     * @param locale needed language code.
     * @return {@link HabitAssignDto} instance.
     */
    @ApiOperation(value = "Get active assign by habit id for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ApiLocale
    @GetMapping("/{id}/assign")
    public ResponseEntity<HabitAssignDto> getHabitAssignByHabitId(
        @ApiIgnore @CurrentUser UserVO userVO,
        @PathVariable Long id,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService.findActiveHabitAssignByUserIdAndHabitId(userVO.getId(), id, locale.getLanguage()));
    }

    /**
     * Method to update active {@link HabitAssignVO} for it's {@link HabitVO} id and
     * current user.
     *
     * @param userVO             {@link UserVO} instance.
     * @param id                 {@link HabitVO} id.
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
