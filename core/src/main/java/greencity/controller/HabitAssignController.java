package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.CurrentUser;
import greencity.annotations.ValidLanguage;
import greencity.constant.AppConstant;
import greencity.constant.HttpStatuses;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitAssignManagementDto;
import greencity.dto.habit.HabitAssignPropertiesDto;
import greencity.dto.habit.HabitAssignStatDto;
import greencity.dto.habit.HabitAssignUserDurationDto;
import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habit.HabitDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.habit.HabitsDateEnrollmentDto;
import greencity.dto.habit.UpdateUserShoppingListDto;
import greencity.dto.habit.UserShoppingAndCustomShoppingListsDto;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarDto;
import greencity.dto.user.UserVO;
import greencity.service.HabitAssignService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/habit/assign")
public class HabitAssignController {
    private final HabitAssignService habitAssignService;

    /**
     * Method which assigns habit for {@link UserVO} with default props.
     *
     * @param habitId {@link HabitVO} id.
     * @param userVO  {@link UserVO} instance.
     * @return {@link ResponseEntity}.
     */
    @ApiOperation(value = "Assign habit with default properties for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @PostMapping("/{habitId}")
    public ResponseEntity<HabitAssignManagementDto> assignDefault(@PathVariable Long habitId,
        @ApiIgnore @CurrentUser UserVO userVO) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(habitAssignService.assignDefaultHabitForUser(habitId, userVO));
    }

    /**
     * Method which assigns habit for {@link UserVO} with custom props.
     *
     * @param habitId                  {@link HabitVO} id.
     * @param userVO                   {@link UserVO} instance.
     * @param habitAssignPropertiesDto {@link HabitAssignPropertiesDto} instance.
     * @return {@link ResponseEntity}.
     */
    @ApiOperation(value = "Assign habit with custom properties for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @PostMapping("/{habitId}/custom")
    public ResponseEntity<HabitAssignManagementDto> assignCustom(@PathVariable Long habitId,
        @ApiIgnore @CurrentUser UserVO userVO,
        @Valid @RequestBody HabitAssignPropertiesDto habitAssignPropertiesDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(habitAssignService.assignCustomHabitForUser(habitId, userVO, habitAssignPropertiesDto));
    }

    /**
     * Method which updates duration of habit assigned for user.
     *
     * @param habitAssignId {@link HabitVO} id.
     * @param userVO        {@link UserVO} instance.
     * @param duration      {@link Integer} with needed duration.
     * @return {@link ResponseEntity}.
     */
    @PutMapping("/{habitAssignId}/update-habit-duration")
    @ApiOperation(value = "Update duration of habit with habitAssignId for user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitAssignUserDurationDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    public ResponseEntity<HabitAssignUserDurationDto> updateHabitAssignDuration(
        @PathVariable Long habitAssignId,
        @ApiIgnore @CurrentUser UserVO userVO,
        @RequestParam @Min(AppConstant.MIN_DAYS_DURATION) @Max(AppConstant.MAX_DAYS_DURATION) Integer duration) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService.updateUserHabitInfoDuration(habitAssignId, userVO.getId(), duration));
    }

    /**
     * Method returns {@link HabitAssignDto} by it's id, current user id and
     * specific language.
     *
     * @param habitAssignId {@link HabitAssignVO} id.
     * @param userVO        {@link UserVO}.
     * @param locale        needed language code.
     * @return {@link HabitAssignDto}.
     */
    @ApiOperation(value = "Get habit assign.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ApiLocale
    @GetMapping("/{habitAssignId}")
    public ResponseEntity<HabitAssignDto> getHabitAssign(@PathVariable Long habitAssignId,
        @ApiIgnore @CurrentUser UserVO userVO, @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService.getByHabitAssignIdAndUserId(habitAssignId, userVO.getId(), locale.getLanguage()));
    }

    /**
     * Method for finding all inprogress, acquired {@link HabitAssignDto}'s for
     * current user.
     *
     * @param userVO {@link UserVO} instance.
     * @param locale needed language code.
     * @return list of {@link HabitAssignDto}.
     */
    @ApiOperation(value = "Get (inprogress, acquired) assigned habits for current user")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitAssignDto.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
    })
    @ApiLocale
    @GetMapping("/allForCurrentUser")
    public ResponseEntity<List<HabitAssignDto>> getCurrentUserHabitAssignsByIdAndAcquired(
        @ApiIgnore @CurrentUser UserVO userVO,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService
                .getAllHabitAssignsByUserIdAndStatusNotCancelled(userVO.getId(), locale.getLanguage()));
    }

    /**
     * Method that return UserShoppingList and CustomShoppingList.
     *
     * @param habitAssignId {@link HabitAssignVO} id.
     * @param userVO        {@link UserVO} instance.
     * @param locale        needed language code.
     * @return User Shopping List and Custom Shopping List.
     */
    @ApiOperation(value = "Get user shopping and custom shopping lists by habitAssignId")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK,
            response = UserShoppingAndCustomShoppingListsDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ApiLocale
    @GetMapping("{habitAssignId}/allUserAndCustomList")
    public ResponseEntity<UserShoppingAndCustomShoppingListsDto> getUserShoppingAndCustomShoppingLists(
        @PathVariable Long habitAssignId,
        @ApiIgnore @CurrentUser UserVO userVO,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService
                .getUserShoppingAndCustomShoppingLists(userVO.getId(), habitAssignId, locale.getLanguage()));
    }

    /**
     * Method that update UserShoppingList and CustomShopping List.
     *
     * @param habitAssignId {@link HabitAssignVO} id.
     * @param userVO        {@link UserVO} instance.
     * @param locale        needed language code.
     * @param listsDto      {@link UserShoppingAndCustomShoppingListsDto} instance.
     */
    @ApiOperation(value = "Update user and custom shopping lists",
        notes = "If the item is already present in the db, the method updates it\n"
            + "If item is not present in the db and id is null, the method attempts to add it to the user\n"
            + "If some items from db are not present in the lists, the method deletes "
            + "them (except for items with DISABLED status).")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ApiLocale
    @PutMapping("{habitAssignId}/allUserAndCustomList")
    public ResponseEntity<ResponseEntity.BodyBuilder> updateUserAndCustomShoppingLists(
        @PathVariable Long habitAssignId,
        @ApiIgnore @CurrentUser UserVO userVO,
        @ApiIgnore @ValidLanguage Locale locale,
        @Valid @RequestBody UserShoppingAndCustomShoppingListsDto listsDto) {
        habitAssignService.fullUpdateUserAndCustomShoppingLists(userVO.getId(), habitAssignId, listsDto,
            locale.getLanguage());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method that return list of UserShoppingLists and CustomShoppingLists for
     * current user, specific language and INPROGRESS status.
     *
     * @param userVO {@link UserVO} instance.
     * @param locale needed language code.
     * @return List of User Shopping Lists and Custom Shopping Lists.
     */
    @ApiOperation(value = "Get list of user shopping list items and custom shopping list items with status INPROGRESS")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK,
            response = UserShoppingAndCustomShoppingListsDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ApiLocale
    @GetMapping("/allUserAndCustomShoppingListsInprogress")
    public ResponseEntity<List<UserShoppingAndCustomShoppingListsDto>> getListOfUserAndCustomShoppingListsInprogress(
        @ApiIgnore @CurrentUser UserVO userVO, @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService
                .getListOfUserAndCustomShoppingListsWithStatusInprogress(userVO.getId(), locale.getLanguage()));
    }

    /**
     * Method to return all inprogress, acquired {@link HabitAssignDto} by it's
     * {@link HabitVO} id.
     *
     * @param habitId {@link HabitVO} id.
     * @param locale  needed language code.
     * @return {@link List} of {@link HabitAssignDto}.
     */
    @ApiOperation(value = "Get all inprogress, acquired assigns by certain habit.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitAssignDto.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @ApiLocale
    @GetMapping("/{habitId}/all")
    public ResponseEntity<List<HabitAssignDto>> getAllHabitAssignsByHabitIdAndAcquired(@PathVariable Long habitId,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService.getAllHabitAssignsByHabitIdAndStatusNotCancelled(habitId,
                locale.getLanguage()));
    }

    /**
     * Method to return {@link HabitAssignVO} by it's {@link HabitVO} id.
     *
     * @param habitId {@link HabitVO} id.
     * @param userVO  {@link UserVO} user.
     * @param locale  needed language code.
     * @return {@link HabitAssignDto} instance.
     */
    @ApiOperation(value = "Get inprogress or acquired assign by habit id for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ApiLocale
    @GetMapping("/{habitId}/active")
    public ResponseEntity<HabitAssignDto> getHabitAssignByHabitId(
        @ApiIgnore @CurrentUser UserVO userVO,
        @PathVariable Long habitId,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService
                .findHabitAssignByUserIdAndHabitId(userVO.getId(), habitId, locale.getLanguage()));
    }

    /**
     * Method to return {@link HabitDto} with more it's information by
     * {@link HabitAssignVO} id.
     *
     * @param habitAssignId {@link HabitAssignVO} id.
     * @param userVO        {@link UserVO} user.
     * @param locale        needed language code.
     * @return {@link HabitDto} instance.
     */
    @ApiOperation(value = "Get habit assign by habit assign id for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ApiLocale
    @GetMapping("/{habitAssignId}/more")
    public ResponseEntity<HabitDto> getUsersHabitByHabitAssignId(
        @ApiIgnore @CurrentUser UserVO userVO,
        @PathVariable Long habitAssignId,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService
                .findHabitByUserIdAndHabitAssignId(userVO.getId(), habitAssignId, locale.getLanguage()));
    }

    /**
     * Method to update inprogress, acquired {@link HabitAssignVO} for it's
     * {@link HabitVO} id and current user.
     *
     * @param userVO             {@link UserVO} instance.
     * @param habitId            {@link HabitVO} id.
     * @param habitAssignStatDto {@link HabitAssignStatDto} instance.
     * @return {@link HabitAssignManagementDto}.
     */
    @ApiOperation(value = "Update inprogress, acquired user habit assign acquired or cancelled status.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PatchMapping("/{habitId}")
    public ResponseEntity<HabitAssignManagementDto> updateAssignByHabitId(
        @ApiIgnore @CurrentUser UserVO userVO,
        @PathVariable Long habitId, @Valid @RequestBody HabitAssignStatDto habitAssignStatDto) {
        return ResponseEntity.status(HttpStatus.OK).body(habitAssignService
            .updateStatusByHabitIdAndUserId(habitId, userVO.getId(), habitAssignStatDto));
    }

    /**
     * Method to enroll {@link HabitAssignVO} for current date.
     *
     * @param habitAssignId - id of {@link HabitAssignVO}.
     * @param userVO        {@link UserVO} user.
     * @param date          - {@link LocalDate} we want to enroll.
     * @param locale        - needed language code.
     * @return {@link HabitStatusCalendarDto}.
     */
    @ApiOperation(value = "Enroll habit assign by habitAssignId for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ApiLocale
    @PostMapping("/{habitAssignId}/enroll/{date}")
    public ResponseEntity<HabitAssignDto> enrollHabit(@PathVariable Long habitAssignId,
        @ApiIgnore @CurrentUser UserVO userVO,
        @PathVariable(value = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService.enrollHabit(habitAssignId, userVO.getId(), date, locale.getLanguage()));
    }

    /**
     * Method to unenroll {@link HabitAssignVO} for defined date.
     *
     * @param habitAssignId - id of {@link HabitAssignVO}.
     * @param userVO        {@link UserVO} user.
     * @param date          - {@link LocalDate} we want to unenroll.
     * @return {@link HabitAssignDto} instance.
     */
    @ApiOperation(value = "Unenroll assigned habit for a specific day.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/{habitAssignId}/unenroll/{date}")
    public ResponseEntity<HabitAssignDto> unenrollHabit(@PathVariable Long habitAssignId,
        @ApiIgnore @CurrentUser UserVO userVO,
        @PathVariable(value = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService.unenrollHabit(habitAssignId, userVO.getId(), date));
    }

    /**
     * Method to find all inprogress {@link HabitAssignVO} on certain
     * {@link LocalDate}.
     *
     * @param userVO {@link UserVO} user.
     * @param date   {@link LocalDate} date to check if has inprogress assigns.
     * @param locale needed language code.
     * @return {@link HabitAssignDto} instance.
     */
    @ApiOperation(value = "Get inprogress user habit assigns on certain date.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitAssignDto.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @ApiLocale
    @GetMapping("/active/{date}")
    public ResponseEntity<List<HabitAssignDto>> getInprogressHabitAssignOnDate(
        @ApiIgnore @CurrentUser UserVO userVO,
        @PathVariable(value = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService
                .findInprogressHabitAssignsOnDate(userVO.getId(), date, locale.getLanguage()));
    }

    /**
     * Method to find all user inprogress activities {@link HabitsDateEnrollmentDto}
     * between the specified {@link LocalDate}s.
     *
     * @param userVO {@link UserVO} user.
     * @param from   The start {@link LocalDate} to retrieve from
     * @param to     The end {@link LocalDate} to retrieve to
     * @param locale needed language code.
     * @return {@link HabitsDateEnrollmentDto} instance.
     */
    @ApiOperation(value = "Get user inprogress activities between the specified dates.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitsDateEnrollmentDto.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @ApiLocale
    @GetMapping("/activity/{from}/to/{to}")
    public ResponseEntity<List<HabitsDateEnrollmentDto>> getHabitAssignBetweenDates(
        @ApiIgnore @CurrentUser UserVO userVO,
        @PathVariable(value = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @PathVariable(value = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService
                .findHabitAssignsBetweenDates(userVO.getId(), from, to, locale.getLanguage()));
    }

    /**
     * Method to cancel inprogress {@link HabitAssignVO} by it's {@link HabitVO} id
     * and current user id.
     *
     * @param habitId - id of {@link HabitVO}.
     * @param userVO  - {@link UserVO} user.
     * @return {@link HabitAssignDto}.
     */
    @ApiOperation(value = "Cancel inprogress user assigned habit.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitAssignDto.class),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PatchMapping("/cancel/{habitId}")
    public ResponseEntity<HabitAssignDto> cancelHabitAssign(@PathVariable Long habitId,
        @ApiIgnore @CurrentUser UserVO userVO) {
        return ResponseEntity.status(HttpStatus.OK).body(habitAssignService.cancelHabitAssign(habitId, userVO.getId()));
    }

    /**
     * Method delete habit assign {@link HabitAssignVO} for current {@link UserVO}
     * by habitAssignId.
     *
     * @param habitAssignId - id of {@link HabitAssignVO}.
     * @param userVO        - {@link UserVO} user.
     */
    @ApiOperation(value = "Delete habit assign by habitAssignId for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/delete/{habitAssignId}")
    public ResponseEntity<ResponseEntity.BodyBuilder> deleteHabitAssign(@PathVariable Long habitAssignId,
        @ApiIgnore @CurrentUser UserVO userVO) {
        habitAssignService.deleteHabitAssign(habitAssignId, userVO.getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method updates user shopping list item {@link UpdateUserShoppingListDto}.
     *
     * @param updateUserShoppingListDto - id of {@link UpdateUserShoppingListDto}.
     */
    @ApiOperation(value = "Update shopping list status for current habit.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PutMapping("/saveShoppingListForHabitAssign")
    public ResponseEntity<ResponseEntity.BodyBuilder> updateShoppingListStatus(
        @RequestBody UpdateUserShoppingListDto updateUserShoppingListDto) {
        habitAssignService.updateUserShoppingListItem(updateUserShoppingListDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}