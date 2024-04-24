package greencity.controller;

import greencity.annotations.CurrentUserId;
import greencity.constant.HttpStatuses;
import greencity.dto.shoppinglistitem.BulkSaveCustomShoppingListItemDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemVO;
import greencity.dto.user.BulkSaveUserShoppingListItemDto;
import greencity.dto.user.UserVO;
import greencity.service.CustomShoppingListItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/custom/shopping-list-items")
public class CustomShoppingListItemController {
    private final CustomShoppingListItemService customShoppingListItemService;

    /**
     * Method for finding all custom shopping list items.
     *
     * @param userId user id.
     * @return list of {@link CustomShoppingListItemVO}
     */
    @Operation(summary = "Get all available custom shopping-list-items")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
    })
    @GetMapping("/{userId}/{habitId}")
    public ResponseEntity<List<CustomShoppingListItemResponseDto>> getAllAvailableCustomShoppingListItems(
        @PathVariable Long userId, @PathVariable Long habitId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(customShoppingListItemService.findAllAvailableCustomShoppingListItems(userId, habitId));
    }

    /**
     * Method saves custom shopping list items for user.
     *
     * @param dto    {@link BulkSaveUserShoppingListItemDto} with list objects to
     *               save
     * @param userId {@link UserVO} id
     * @return new {@link ResponseEntity}
     * @author Bogdan Kuzenko
     */
    @Operation(summary = "Save one or multiple custom Shopping list item for current user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
    })
    @PostMapping("/{userId}/{habitAssignId}/custom-shopping-list-items")
    public ResponseEntity<List<CustomShoppingListItemResponseDto>> saveUserCustomShoppingListItems(
        @Valid @RequestBody BulkSaveCustomShoppingListItemDto dto,
        @Parameter(description = "Id of current user. Cannot be empty.") @PathVariable @CurrentUserId Long userId,
        @PathVariable Long habitAssignId) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(customShoppingListItemService.save(dto, userId, habitAssignId));
    }

    /**
     * Method updated user custom shopping list items to status DONE.
     *
     * @param userId     {@link UserVO} id
     * @param itemId     {@link Long} with needed item id.
     * @param itemStatus {@link String} with needed item status.
     * @return new {@link ResponseEntity}
     * @author Bogdan Kuzenko
     */
    @Operation(summary = "Update custom shopping list items status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER,
            content = @Content(examples = @ExampleObject(HttpStatuses.SEE_OTHER))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @PatchMapping("/{userId}/custom-shopping-list-items")
    public ResponseEntity<CustomShoppingListItemResponseDto> updateItemStatus(@PathVariable @CurrentUserId Long userId,
        @RequestParam("itemId") Long itemId,
        @RequestParam("status") String itemStatus) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(customShoppingListItemService.updateItemStatus(userId, itemId, itemStatus));
    }

    /**
     * Method updates user's shopping list items to status DONE.
     *
     * @param userId {@link UserVO} id
     * @param itemId {@link Long} with needed item id.
     * @author Volodia Lesko
     */
    @Operation(summary = "Update shopping list item status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER,
            content = @Content(examples = @ExampleObject(HttpStatuses.SEE_OTHER))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @PatchMapping("/{userId}/done")
    public void updateItemStatusToDone(@PathVariable @CurrentUserId Long userId,
        @RequestParam("itemId") Long itemId) {
        customShoppingListItemService.updateItemStatusToDone(userId, itemId);
    }

    /**
     * Method for delete user custom shopping list items.
     *
     * @param ids    string with objects id for deleting.
     * @param userId {@link UserVO} id
     * @return new {@link ResponseEntity}
     */
    @Operation(summary = "Delete user custom shopping list items")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER,
            content = @Content(examples = @ExampleObject(HttpStatuses.SEE_OTHER))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
    })
    @DeleteMapping("/{userId}/custom-shopping-list-items")
    public ResponseEntity<List<Long>> bulkDeleteCustomShoppingListItems(
        @Parameter(description = "Ids of custom shopping-list-items separated by a comma \n e.g. 1,2",
            required = true) @RequestParam String ids,
        @PathVariable @CurrentUserId Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(customShoppingListItemService.bulkDelete(ids));
    }

    /**
     * Method returns all user's custom shopping items by status if is defined.
     *
     * @param userId {@link Long} id
     * @param status {@link String} status
     * @return list of {@link ResponseEntity}
     * @author Max Bohonko
     */
    @Operation(summary = "Get all user's custom shopping items")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER,
            content = @Content(examples = @ExampleObject(HttpStatuses.SEE_OTHER))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @GetMapping("/{userId}/custom-shopping-list-items")
    public ResponseEntity<List<CustomShoppingListItemResponseDto>> getAllCustomShoppingItemsByStatus(
        @PathVariable @CurrentUserId Long userId,
        @Parameter(description = "Available values : ACTIVE, DONE, DISABLED, INPROGRESS."
            + " Leave this field empty if you need items with any status") @RequestParam(
                required = false) String status) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(customShoppingListItemService.findAllUsersCustomShoppingListItemsByStatus(userId, status));
    }
}
