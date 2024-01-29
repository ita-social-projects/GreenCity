package greencity.service;

import greencity.dto.shoppinglistitem.BulkSaveCustomShoppingListItemDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemVO;
import greencity.dto.user.UserVO;

import java.util.List;

/**
 * Provides the interface to manage {@code CustomShoppingList} entity.
 *
 * @author Bogdan Kuzenko
 */
public interface CustomShoppingListItemService {
    /**
     * Method saves list of custom shopping list items for user.
     *
     * @param bulkSaveCustomShoppingListItemDto {@link BulkSaveCustomShoppingListItemDto}
     *                                          with objects list for saving.
     * @param userId                            {@link UserVO} current user id
     * @return list of saved {@link CustomShoppingListItemResponseDto}
     */
    List<CustomShoppingListItemResponseDto> save(BulkSaveCustomShoppingListItemDto bulkSaveCustomShoppingListItemDto,
        Long userId, Long habitAssignId);

    /**
     * Method for finding all custom shopping list items.
     *
     * @return list of {@link CustomShoppingListItemResponseDto}
     */
    List<CustomShoppingListItemResponseDto> findAll();

    /**
     * Method for finding all custom shopping list item for one user.
     *
     * @param userId user id.
     * @return list of {@link CustomShoppingListItemResponseDto}
     */
    List<CustomShoppingListItemResponseDto> findAllByUserAndHabit(Long userId, Long habitId);

    /**
     * Method for finding one custom shopping list item by id.
     *
     * @param id - custom shopping list item id.
     * @return {@link CustomShoppingListItemResponseDto}
     */
    CustomShoppingListItemResponseDto findById(Long id);

    /**
     * Method update custom shopping items status.
     *
     * @param userId     {@link Long} user id.
     * @param itemId     {@link Long} custom shopping list item id.
     * @param itemStatus {@link String} custom shopping list item status.
     * @return {@link CustomShoppingListItemResponseDto}
     */
    CustomShoppingListItemResponseDto updateItemStatus(Long userId, Long itemId, String itemStatus);

    /**
     * Method updates user's shopping item status to DONE.
     *
     * @param userId {@link Long} user id.
     * @param itemId {@link Long} custom shopping list item id.
     */
    void updateItemStatusToDone(Long userId, Long itemId);

    /**
     * Method for deleted list of custom shopping list items.
     *
     * @param ids string with objects id for deleting.
     * @return list ids of deleted custom shopping list items
     */
    List<Long> bulkDelete(String ids);

    /**
     * Method for finding all custom shopping list items.
     *
     * @param userId user id.
     * @return list of {@link CustomShoppingListItemVO}
     */
    List<CustomShoppingListItemResponseDto> findAllAvailableCustomShoppingListItems(Long userId, Long habitId);

    /**
     * Method for finding all custom shopping list items with not DISABLED status by
     * habitAssignId.
     *
     * @param userId        user id.
     * @param habitAssignId habitAssign id.
     * @return list of {@link CustomShoppingListItemVO}
     */
    List<CustomShoppingListItemResponseDto> findAllAvailableCustomShoppingListItemsByHabitAssignId(Long userId,
        Long habitAssignId);

    /**
     * Method for finding custom shopping list items by userId and habitId and
     * INPROGRESS status.
     *
     * @param userId  user id.
     * @param habitId habit id.
     * @return list of {@link CustomShoppingListItemResponseDto}
     */
    List<CustomShoppingListItemResponseDto> findAllCustomShoppingListItemsWithStatusInProgress(Long userId,
        Long habitId);

    /**
     * Method returns all user's custom shopping items by status(if is defined).
     *
     * @param userId user id.
     * @return list of {@link CustomShoppingListItemVO}
     */
    List<CustomShoppingListItemResponseDto> findAllUsersCustomShoppingListItemsByStatus(Long userId, String status);
}
