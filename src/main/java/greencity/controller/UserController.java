package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.constant.AppConstant;
import greencity.constant.HttpStatuses;
import greencity.constant.ValidationConstants;
import greencity.dto.PageableDto;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.goal.BulkCustomGoalDto;
import greencity.dto.goal.BulkSaveCustomGoalDto;
import greencity.dto.goal.CustomGoalResponseDto;
import greencity.dto.goal.GoalDto;
import greencity.dto.habitstatistic.CalendarUsefulHabitsDto;
import greencity.dto.habitstatistic.HabitCreateDto;
import greencity.dto.habitstatistic.HabitDto;
import greencity.dto.habitstatistic.HabitIdDto;
import greencity.dto.user.*;
import greencity.entity.User;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.UserStatus;
import greencity.service.CustomGoalService;
import greencity.service.UserService;
import greencity.service.UserValidationService;
import greencity.service.impl.HabitStatisticServiceImpl;
import io.swagger.annotations.*;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Validated
public class UserController {
    private UserService userService;
    private UserValidationService userValidationService;
    private HabitStatisticServiceImpl habitStatisticServiceImpl;
    private CustomGoalService customGoalService;

    /**
     * The method which update user status.
     * Parameter principal are ignored because Spring automatically provide the Principal object.
     *
     * @param userStatusDto - dto with updated filed.
     * @return {@link UserStatusDto}
     * @author Rostyslav Khasanov
     */
    @ApiOperation(value = "Update status of user")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = UserStatus.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping("status")
    public ResponseEntity<UserStatusDto> updateStatus(
        @Valid @RequestBody UserStatusDto userStatusDto, @ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                userService.updateStatus(
                    userStatusDto.getId(), userStatusDto.getUserStatus(), principal.getName()));
    }

    /**
     * The method which update user role.
     * Parameter principal are ignored because Spring automatically provide the Principal object.
     *
     * @param userRoleDto - dto with updated field.
     * @return {@link UserRoleDto}
     * @author Rostyslav Khasanov
     */
    @ApiOperation(value = "Update role of user")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = UserRoleDto.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping("role")
    public ResponseEntity<UserRoleDto> updateRole(
        @Valid @RequestBody UserRoleDto userRoleDto, @ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                userService.updateRole(
                    userRoleDto.getId(), userRoleDto.getRole(), principal.getName()));
    }

    /**
     * The method which return list of users by page.
     * Parameter pageable ignored because swagger ui shows the wrong params,
     * instead they are explained in the {@link ApiPageable}.
     *
     * @param pageable - pageable configuration.
     * @return list of {@link PageableDto}
     * @author Rostyslav Khasanov
     */
    @ApiOperation(value = "Get users by page")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = PageableDto.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @ApiPageable
    @GetMapping("all")
    public ResponseEntity<PageableDto> getAllUsers(@ApiIgnore Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByPage(pageable));
    }

    /**
     * The method which return array of existing roles.
     *
     * @return {@link RoleDto}
     * @author Rostyslav Khasanov
     */
    @ApiOperation(value = "Get all available roles")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = RoleDto.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("roles")
    public ResponseEntity<RoleDto> getRoles() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getRoles());
    }

    /**
     * The method which return array of existing {@link EmailNotification}.
     *
     * @return {@link EmailNotification} array
     * @author Nazar Vladyka
     */
    @ApiOperation(value = "Get all available email notifications statuses")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = EmailNotification[].class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("emailNotifications")
    public ResponseEntity<List<EmailNotification>> getEmailNotifications() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getEmailNotificationsStatuses());
    }

    /**
     * The method which return list of users by filter.
     * Parameter pageable ignored because swagger ui shows the wrong params,
     * instead they are explained in the {@link ApiPageable}.
     *
     * @param filterUserDto dto which contains fields with filter criteria.
     * @param pageable      - pageable configuration.
     * @return {@link PageableDto}
     * @author Rostyslav Khasanov
     */
    @ApiOperation(value = "Filter all user by search criteria")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = PageableDto.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @ApiPageable
    @PostMapping("filter")
    public ResponseEntity<PageableDto> getUsersByFilter(
        @ApiIgnore Pageable pageable, @RequestBody FilterUserDto filterUserDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsersByFilter(filterUserDto, pageable));
    }

    /**
     * Get {@link User} dto by principal (email) from access token.
     *
     * @return {@link UserUpdateDto}.
     * @author Nazar Stasyuk
     */
    @ApiOperation(value = "Get User dto by principal (email) from access token")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = UserUpdateDto.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping
    public ResponseEntity<UserUpdateDto> getUserByPrincipal(@ApiIgnore @AuthenticationPrincipal Principal principal) {
        String email = principal.getName();
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserUpdateDtoByEmail(email));
    }

    /**
     * Update {@link User}.
     *
     * @return {@link ResponseEntity}.
     * @author Nazar Stasyuk
     */
    @ApiOperation(value = "Update User")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PutMapping
    public ResponseEntity updateUser(@Valid @RequestBody UserUpdateDto dto,
                                     @ApiIgnore @AuthenticationPrincipal Principal principal) {
        String email = principal.getName();
        userService.update(dto, email);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * Method for finding all {@link User} habits.
     *
     * @param userId    {@link User} id.
     * @param principal Principal with {@link User} email.
     * @return list of {@link HabitDto}
     */
    @GetMapping("/{userId}/habits")
    public ResponseEntity<List<HabitDto>> getUserHabits(@PathVariable Long userId, @ApiIgnore Principal principal,
                                          @ApiParam(value = "Code of the needed language.")
                                                            @RequestParam String language) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(habitStatisticServiceImpl.findAllHabitsAndTheirStatistics(
                userValidationService.userValidForActions(principal, userId).getId(), true, language));
    }

    /**
     * Method for finding {@link CalendarUsefulHabitsDto} by {@link User} email.
     * Parameter principal are ignored because Spring automatically provide the Principal object.
     *
     * @param userId    {@link User} id.
     * @param principal - Principal with {@link User} email.
     * @return {@link CalendarUsefulHabitsDto} instance.
     */
    @ApiOperation(value = "Find statistic about user habits.")
    @GetMapping("/{userId}/habits/statistic")
    public ResponseEntity<CalendarUsefulHabitsDto> findInfoAboutUserHabits(
        @PathVariable Long userId, @ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitStatisticServiceImpl.getInfoAboutUserHabits(
                userValidationService.userValidForActions(principal, userId).getId()));
    }

    /**
     * Method returns list of user goals for specific language.
     *
     * @param principal - authentication principal
     * @param language  - needed language code
     * @return {@link ResponseEntity}.
     * @author Vitalii Skolozdra
     */
    @ApiOperation(value = "Get goals of current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/{userId}/goals")
    public ResponseEntity<List<UserGoalResponseDto>> getUserGoals(
        @ApiIgnore
            Principal principal,
        @ApiParam("Id of current user. Cannot be empty.")
        @PathVariable Long userId,
        @ApiParam(value = "Code of the needed language.", defaultValue = AppConstant.DEFAULT_LANGUAGE_CODE)
        @RequestParam(required = false, defaultValue = AppConstant.DEFAULT_LANGUAGE_CODE) String language) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.getUserGoals(userValidationService.userValidForActions(principal, userId), language));
    }

    /**
     * Method returns list user custom goals.
     *
     * @param userId    {@link User} id
     * @param principal - authentication principal
     * @return list of {@link ResponseEntity}
     * @author Bogdan Kuzenko
     */
    @ApiOperation(value = "Get all user custom goals.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/{userId}/customGoals")
    public ResponseEntity<List<CustomGoalResponseDto>> findAllByUser(@PathVariable Long userId,
                                                                     @ApiIgnore Principal principal) {
        userValidationService.userValidForActions(principal, userId);
        return ResponseEntity.status(HttpStatus.OK).body(customGoalService.findAllByUser(userId));
    }

    /**
     * Method saves custom goals for user.
     *
     * @param dto       {@link BulkSaveUserGoalDto} with list objects to save
     * @param principal - authentication principal
     * @param userId    {@link User} id
     * @return new {@link ResponseEntity}
     * @author Bogdan Kuzenko
     */
    @ApiOperation(value = "Save one or multiple custom goals for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/{userId}/customGoals")
    public ResponseEntity<List<CustomGoalResponseDto>> saveUserCustomGoals(
        @Valid @RequestBody BulkSaveCustomGoalDto dto,
        @ApiIgnore Principal principal,
        @ApiParam("Id of current user. Cannot be empty.")
        @PathVariable Long userId) {
        userValidationService.userValidForActions(principal, userId);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(customGoalService.save(dto, userService.findById(userId)));
    }

    /**
     * Method updated user custom goals.
     *
     * @param userId    {@link User} id
     * @param dto       {@link BulkCustomGoalDto} with list objects for update
     * @param principal - authentication principal
     * @return new {@link ResponseEntity}
     * @author Bogdan Kuzenko
     */
    @ApiOperation(value = "Update user custom goals")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = UserRoleDto.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping("/{userId}/customGoals")
    public ResponseEntity<List<CustomGoalResponseDto>> updateBulk(@PathVariable Long userId,
                                                                  @Valid @RequestBody BulkCustomGoalDto dto,
                                                                  @ApiIgnore Principal principal) {
        userValidationService.userValidForActions(principal, userId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(customGoalService.updateBulk(dto));
    }

    /**
     * Method for delete user custom goals.
     *
     * @param ids       string with objects id for deleting.
     * @param userId    {@link User} id
     * @param principal - authentication principal
     * @return new {@link ResponseEntity}
     */
    @ApiOperation(value = "Delete user custom goals")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = Long.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @DeleteMapping("/{userId}/customGoals")
    public ResponseEntity<List<Long>> bulkDeleteCustomGoals(
        @ApiParam(value = "Ids of custom goals separated by a comma \n e.g. 1,2", required = true)
        @RequestParam String ids,
        @PathVariable Long userId,
        @ApiIgnore Principal principal) {
        userValidationService.userValidForActions(principal, userId);
        return ResponseEntity.status(HttpStatus.OK).body(customGoalService.bulkDelete(ids));
    }

    /**
     * Method returns list of available (not ACTIVE) goals for user.
     *
     * @param principal - authentication principal
     * @param language  - needed language code
     * @return {@link ResponseEntity}.
     * @author Vitalii Skolozdra
     */
    @ApiOperation(value = "Get available goals for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/{userId}/goals/available")
    public ResponseEntity<List<GoalDto>> getAvailableGoals(
        @ApiIgnore Principal principal,
        @ApiParam("Id of current user. Cannot be empty.")
        @PathVariable Long userId,
        @ApiParam(value = "Code of the needed language.", defaultValue = AppConstant.DEFAULT_LANGUAGE_CODE)
        @RequestParam(required = false, defaultValue = AppConstant.DEFAULT_LANGUAGE_CODE) String language) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.getAvailableGoals(userValidationService.userValidForActions(principal, userId),
                language));
    }

    /**
     * Method returns list of available (not ACTIVE) custom goals for user.
     *
     * @param principal - authentication principal
     * @return {@link ResponseEntity}.
     * @author Vitalii Skolozdra
     */
    @ApiOperation(value = "Get available custom goals for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/{userId}/customGoals/available")
    public ResponseEntity<List<CustomGoalResponseDto>> getAvailableCustomGoals(
        @ApiIgnore
            Principal principal,
        @ApiParam("Id of current user. Cannot be empty.")
        @PathVariable Long userId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.getAvailableCustomGoals(userValidationService.userValidForActions(principal, userId)));
    }


    /**
     * Method updates goal status.
     *
     * @param language - needed language code
     * @return new {@link ResponseEntity}.
     * @author Vitalii Skolozdra
     */
    @ApiOperation(value = "Change status of one of the goals for current user to DONE.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PatchMapping("/{userId}/goals/{goalId}")
    public ResponseEntity<UserGoalResponseDto> updateUserGoalStatus(
        @ApiParam("Id of current user. Cannot be empty.")
        @PathVariable Long userId,
        @ApiParam("Id of the UserGoal that belongs to current user. Cannot be empty.")
        @PathVariable Long goalId,
        @ApiParam(value = "Code of the needed language.", defaultValue = AppConstant.DEFAULT_LANGUAGE_CODE)
        @RequestParam(required = false, defaultValue = AppConstant.DEFAULT_LANGUAGE_CODE) String language,
        @ApiIgnore
            Principal principal) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(userService
                .updateUserGoalStatus(userValidationService.userValidForActions(principal, userId), goalId, language));
    }

    /**
     * Method saves goals, chosen by user.
     *
     * @param dto      - dto with goals, chosen by user.
     * @param language - needed language code
     * @return new {@link ResponseEntity}.
     * @author Vitalii Skolozdra
     */
    @ApiOperation(value = "Save one or multiple goals for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/{userId}/goals")
    public ResponseEntity<List<UserGoalResponseDto>> saveUserGoals(
        @Valid @RequestBody BulkSaveUserGoalDto dto,
        @ApiIgnore
            Principal principal,
        @ApiParam("Id of current user. Cannot be empty.")
        @PathVariable Long userId,
        @ApiParam(value = "Code of the needed language.", defaultValue = AppConstant.DEFAULT_LANGUAGE_CODE)
        @RequestParam(required = false, defaultValue = AppConstant.DEFAULT_LANGUAGE_CODE) String language) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(userService.saveUserGoals(userValidationService
                .userValidForActions(principal, userId), dto, language));
    }

    /**
     * Method returns list of available (not ACTIVE) habit dictionary for user.
     *
     * @param principal - authentication principal
     * @return {@link ResponseEntity}.
     * @author Kuzenko Bogdan
     */
    @ApiOperation(value = "Get available habit dictionary for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/{userId}/habit-dictionary/available")
    public ResponseEntity<List<HabitDictionaryDto>> getAvailableHabitDictionary(
        @ApiIgnore
            Principal principal,
        @ApiParam("Id of current user. Cannot be empty.")
        @PathVariable Long userId,
        @ApiParam(value = "Code of the needed language.")
        @RequestParam String language) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.getAvailableHabitDictionary(
                userValidationService.userValidForActions(principal, userId), language));
    }

    /**
     * Method saves habit, chosen by user.
     *
     * @param dto       - dto with habits, chosen by user.
     * @param userId    id current user.
     * @param principal authentication principal.
     * @return {@link ResponseEntity}
     */
    @ApiOperation(value = "Save one or multiple habits for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/{userId}/habit")
    public ResponseEntity<List<HabitCreateDto>> saveUserHabits(
        @Valid @RequestBody List<HabitIdDto> dto,
        @ApiParam("Id of current user. Cannot be empty.")
        @PathVariable Long userId,
        @ApiParam(value = "Code of the needed language.")
        @RequestParam String language,
        @ApiIgnore
            Principal principal) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(userService.createUserHabit(userValidationService.userValidForActions(principal, userId),
                    dto, language));
    }

    /**
     * Method deletes habit, chosen by user.
     *
     * @param habitId   id with habits, chosen by user.
     * @param userId    id current user.
     * @param principal authentication principal.
     */
    @ApiOperation(value = "Delete habit")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @DeleteMapping("/{userId}/habit/{habitId}")
    public void deleteHabit(
        @ApiParam("Id habit of current user. Cannot be empty.")
        @PathVariable Long habitId,
        @ApiParam("Id of current user. Cannot be empty.")
        @PathVariable Long userId,
        @ApiIgnore
            Principal principal) {
        userService.deleteHabitByUserIdAndHabitDictionary(userId, habitId);
        ResponseEntity.status(HttpStatus.OK);
    }

    /**
     * Method for deleting user goals.
     *
     * @param ids       string with objects id for deleting.
     * @param userId    {@link User} id
     * @param principal - authentication principal
     * @return new {@link ResponseEntity}
     * @author Bogdan Kuzenko
     */
    @ApiOperation(value = "Delete user goal")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = Long.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @DeleteMapping("/{userId}/userGoals")
    public ResponseEntity<List<Long>> bulkDeleteUserGoals(
        @ApiParam(value = "Ids of user goals separated by a comma \n e.g. 1,2", required = true)
        @Pattern(regexp = "^\\d+(,\\d+)*$", message = ValidationConstants.BAD_COMMA_SEPARATED_NUMBERS)
        @RequestParam String ids,
        @PathVariable Long userId,
        @ApiIgnore Principal principal) {
        userValidationService.userValidForActions(principal, userId);
        return ResponseEntity.status(HttpStatus.OK).body(userService
            .deleteUserGoals(ids));
    }
}
