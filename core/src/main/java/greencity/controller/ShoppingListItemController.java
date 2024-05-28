package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.CurrentUser;
import greencity.annotations.CurrentUserId;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.constant.ValidationConstants;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.shoppinglistitem.ShoppingListItemRequestDto;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.dto.user.UserVO;
import greencity.service.ShoppingListItemService;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import java.util.List;
import java.util.Locale;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/shopping-list-items")
public class ShoppingListItemController {
    private final ShoppingListItemService shoppingListItemService;

    /**
     * Method saves shopping list items, chosen by user.
     *
     * @param dto    - dto with Items, chosen by user.
     * @param locale - needed language code
     * @return new {@link ResponseEntity}.
     * @author Vitalii Skolozdra
     */
    @Operation(summary = "Save one or multiple shopping list items for current user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @PostMapping
    @ApiLocale
    public ResponseEntity<List<UserShoppingListItemResponseDto>> saveUserShoppingListItems(
        @Valid @RequestBody List<ShoppingListItemRequestDto> dto,
        @Parameter(hidden = true) @CurrentUser UserVO user,
        Long habitId,
        @Parameter(hidden = true) @ValidLanguage Locale locale) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(shoppingListItemService.saveUserShoppingListItems(user.getId(), habitId, dto, locale.getLanguage()));
    }

    /**
     * Method finds shoppingList saved by user in specific language.
     *
     * @param locale  {@link Locale} with needed language code.
     * @param habitId {@link Long} with needed habit id.
     * @return List of {@link UserShoppingListItemResponseDto}.
     * @author Dmytro Khonko
     */
    @Operation(description = "Get user`s shopping list.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @GetMapping("/habits/{habitId}/shopping-list")
    @ApiLocale
    public ResponseEntity<List<UserShoppingListItemResponseDto>> getShoppingListItemsAssignedToUser(
        @Parameter(hidden = true) @CurrentUser UserVO user,
        @Parameter(
            description = "Id of the Habit that belongs to current user. Cannot be empty.") @PathVariable Long habitId,
        @Parameter(hidden = true) @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(shoppingListItemService.getUserShoppingList(user.getId(), habitId, locale.getLanguage()));
    }

    /**
     * Method deletes from shoppingList item saved by user.
     *
     * @param shoppingListItemId {@link Long} with needed shopping list item id.
     * @param habitId            {@link Long} with needed habit id.
     * @author Dmytro Khonko
     */

    @Operation(summary = "Delete from shopping list")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @DeleteMapping
    public void delete(
        @Parameter(hidden = true) @CurrentUser UserVO user, Long habitId, Long shoppingListItemId) {
        shoppingListItemService.deleteUserShoppingListItemByItemIdAndUserIdAndHabitId(shoppingListItemId, user.getId(),
            habitId);
    }

    /**
     * Method updates shopping list item status to 'DONE'.
     *
     * @param locale - needed language code
     * @return new {@link ResponseEntity}.
     * @author Vitalii Skolozdra
     */
    @Operation(summary = "Change status of one of the shopping list item for current user to DONE.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @PatchMapping("/{userShoppingListItemId}")
    @ApiLocale
    public ResponseEntity<UserShoppingListItemResponseDto> updateUserShoppingListItemStatus(
        @Parameter(hidden = true) @CurrentUser UserVO user,
        @Parameter(description = "Id of the UserShoppingListItems that belongs to current user."
            + " Cannot be empty.") @PathVariable Long userShoppingListItemId,
        @Parameter(hidden = true) @ValidLanguage Locale locale) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(shoppingListItemService.updateUserShopingListItemStatus(user.getId(), userShoppingListItemId,
                locale.getLanguage()));
    }

    /**
     * Method updates user shopping list item status.
     *
     * @param locale - needed language code
     * @return new {@link ResponseEntity}.
     * @author Mykola Danylko
     */
    @Operation(summary = "Change status of one of the user shopping list item for current user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK, content = @Content(
            array = @ArraySchema(schema = @Schema(implementation = UserShoppingListItemResponseDto.class)))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
    })
    @PatchMapping("/{userShoppingListItemId}/status/{status}")
    @ApiLocale
    public ResponseEntity<List<UserShoppingListItemResponseDto>> updateUserShoppingListItemStatus(
        @Parameter(hidden = true) @CurrentUser UserVO user,
        @Parameter(description = "Id of the userShoppingListItem that belongs to current user."
            + " Cannot be empty.") @PathVariable(value = "userShoppingListItemId") Long userShoppingListItemId,
        @PathVariable(value = "status") String status,
        @Parameter(hidden = true) @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK).body(shoppingListItemService
            .updateUserShoppingListItemStatus(user.getId(), userShoppingListItemId, locale.getLanguage(), status));
    }

    /**
     * Method for deleting user shopping list item.
     *
     * @param ids string with objects id for deleting.
     * @return new {@link ResponseEntity}
     * @author Bogdan Kuzenko
     */
    @Operation(summary = "Delete user shopping list item")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = Long.class))),
        @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER,
            content = @Content(examples = @ExampleObject(HttpStatuses.SEE_OTHER))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
    })
    @DeleteMapping("/user-shopping-list-items")
    public ResponseEntity<List<Long>> bulkDeleteUserShoppingListItems(
        @Parameter(description = "Ids of user shopping list items separated by a comma \n e.g. 1,2",
            required = true) @Pattern(
                regexp = "^\\d+(,\\d+)++$",
                message = ValidationConstants.BAD_COMMA_SEPARATED_NUMBERS) @RequestParam String ids,
        @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK).body(shoppingListItemService
            .deleteUserShoppingListItems(ids));
    }

    /**
     * Method returns list user custom shopping-list.
     *
     * @param userId {@link UserVO} id
     * @return list of {@link ResponseEntity}
     * @author Bogdan Kuzenko
     */
    @Operation(summary = "Get all user shopping-list-items with 'INPROGRESS' status.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
    })
    @GetMapping("/{userId}/get-all-inprogress")
    public ResponseEntity<List<ShoppingListItemDto>> findInProgressByUserId(
        @PathVariable @CurrentUserId Long userId, @RequestParam(name = "lang") String code) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(shoppingListItemService.findInProgressByUserIdAndLanguageCode(userId, code));
    }
}
