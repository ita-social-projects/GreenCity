package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.CurrentUser;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.constant.ValidationConstants;
import greencity.dto.shoppinglistitem.ShoppingListItemRequestDto;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.dto.user.UserVO;
import greencity.service.ShoppingListItemService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.Locale;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

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
    @ApiOperation(value = "Save one or multiple shopping list items for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping
    @ApiLocale
    public ResponseEntity<List<UserShoppingListItemResponseDto>> saveUserShoppingListItems(
        @Valid @RequestBody List<ShoppingListItemRequestDto> dto,
        @ApiIgnore @CurrentUser UserVO user,
        Long habitId,
        @ApiIgnore @ValidLanguage Locale locale) {
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
    @ApiOperation(value = "Get user`s shopping list.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/habits/{habitId}/shopping-list")
    @ApiLocale
    public ResponseEntity<List<UserShoppingListItemResponseDto>> getShoppingListItemsAssignedToUser(
        @ApiIgnore @CurrentUser UserVO user,
        @ApiParam("Id of the Habit that belongs to current user. Cannot be empty.") @PathVariable Long habitId,
        @ApiIgnore @ValidLanguage Locale locale) {
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

    @ApiOperation(value = "Delete from shopping list")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @DeleteMapping
    public void delete(
        @ApiIgnore @CurrentUser UserVO user, Long habitId, Long shoppingListItemId) {
        shoppingListItemService.deleteUserShoppingListItemByItemIdAndUserIdAndHabitId(shoppingListItemId, user.getId(),
            habitId);
    }

    /**
     * Method updates shopping list item status.
     *
     * @param locale - needed language code
     * @return new {@link ResponseEntity}.
     * @author Vitalii Skolozdra
     */
    @ApiOperation(value = "Change status of one of the shopping list item for current user to DONE.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PatchMapping("/{userShoppingListItemId}")
    @ApiLocale
    public ResponseEntity<UserShoppingListItemResponseDto> updateUserShoppingListItemStatus(
        @ApiIgnore @CurrentUser UserVO user,
        @ApiParam("Id of the UserShoppingListItems that belongs to current user. Cannot be empty.")
        @PathVariable Long userShoppingListItemId,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(shoppingListItemService.updateUserShopingListItemStatus(user.getId(), userShoppingListItemId,
                locale.getLanguage()));
    }

    /**
     * Method for deleting user shopping list item.
     *
     * @param ids string with objects id for deleting.
     * @return new {@link ResponseEntity}
     * @author Bogdan Kuzenko
     */
    @ApiOperation(value = "Delete user shopping list item")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = Long.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @DeleteMapping("/user-shopping-list-items")
    public ResponseEntity<List<Long>> bulkDeleteUserShoppingListItems(
        @ApiParam(value = "Ids of user shopping list items separated by a comma \n e.g. 1,2", required = true) @Pattern(
            regexp = "^\\d+(,\\d+)++$",
            message = ValidationConstants.BAD_COMMA_SEPARATED_NUMBERS) @RequestParam String ids,
        @ApiIgnore @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK).body(shoppingListItemService
            .deleteUserShoppingListItems(ids));
    }
}
