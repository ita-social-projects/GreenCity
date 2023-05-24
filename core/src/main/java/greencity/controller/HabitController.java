package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ApiPageableWithLocale;
import greencity.annotations.CurrentUser;
import greencity.annotations.ImageValidation;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.dto.habit.AddCustomHabitDtoResponse;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.dto.user.UserProfilePictureDto;
import greencity.dto.user.UserVO;
import greencity.service.HabitService;
import greencity.service.TagsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/habit")
public class HabitController {
    private final HabitService habitService;
    private final TagsService tagsService;

    /**
     * Method finds {@link HabitVO} by given id with locale translation.
     *
     * @param id     of {@link HabitVO}.
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
     * Method finds shoppingList for habit in specific language.
     *
     * @param locale {@link Locale} with needed language code.
     * @param id     {@link Long} with needed habit id.
     * @return List of {@link ShoppingListItemDto}.
     */
    @ApiOperation(value = "Get shopping list.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
    })
    @GetMapping("{id}/shopping-list")
    @ApiLocale
    public ResponseEntity<List<ShoppingListItemDto>> getShoppingListItems(
        @PathVariable Long id,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK).body(
            habitService.getShoppingListForHabit(id, locale.getLanguage()));
    }

    /**
     * Method finds all habits by tags and language code.
     *
     * @param locale   {@link Locale} with needed language code.
     * @param pageable {@link Pageable} instance.
     * @param tags     {@link List} of {@link String}
     * @return Pageable of {@link HabitDto}.
     */
    @ApiOperation(value = "Find all habits by tags and language code.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
    })
    @GetMapping("/tags/search")
    @ApiPageableWithLocale
    public ResponseEntity<PageableDto<HabitDto>> getAllByTagsAndLanguageCode(
        @ApiIgnore @ValidLanguage Locale locale,
        @RequestParam List<String> tags,
        @ApiIgnore Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(
            habitService.getAllByTagsAndLanguageCode(pageable, tags, locale.getLanguage()));
    }

    /**
     * The method which returns all habit's tags.
     *
     * @return list of {@link String} (tag's names).
     * @author Markiyan Derevetskyi
     */
    @ApiOperation(value = "Find all habits tags")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
    })
    @GetMapping("/tags")
    @ApiLocale
    public ResponseEntity<List<String>> findAllHabitsTags(@ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK).body(tagsService.findAllHabitsTags(locale.getLanguage()));
    }

    /**
     * Method for creating Custom Habit.
     *
     * @param request {@link AddCustomHabitDtoRequest} - new custom habit dto.
     * @return dto {@link AddCustomHabitDtoResponse}
     *
     * @author Lilia Mokhnatska.
     */
    @ApiOperation(value = "Add new custom habit.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = AddCustomHabitDtoResponse.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND),
    })
    @PostMapping(value = "/custom",
        consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<AddCustomHabitDtoResponse> addCustomHabit(
        @RequestPart @Valid AddCustomHabitDtoRequest request,
        @ApiParam(value = "Image of habit") @ImageValidation @RequestPart(required = false) MultipartFile image,
        @ApiIgnore Principal principal) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(habitService.addCustomHabit(request, image, principal.getName()));
    }

    /**
     * Retrieves the list of profile pictures of the user's friends (which have
     * INPROGRESS assign to the habit).
     *
     * @param habitId {@link HabitVO} id.
     * @param userVO  {@link UserVO}.
     * @return List of friends profile picture.
     */
    @ApiOperation(
        value = "Retrieves the list of profile pictures of the user's friends "
            + "(which have INPROGRESS assign to the habit).")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND),
    })
    @GetMapping("/{habitId}/friends/profile-pictures")
    public ResponseEntity<List<UserProfilePictureDto>> getFriendsAssignedToHabitProfilePictures(
        @PathVariable Long habitId,
        @ApiIgnore @CurrentUser UserVO userVO) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitService.getFriendsAssignedToHabitProfilePictures(habitId, userVO.getId()));
    }
}
