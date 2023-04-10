package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.shoppinglistitem.*;
import greencity.dto.habit.HabitVO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.UserShoppingListItemDto;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.dto.user.UserShoppingListItemVO;
import greencity.dto.user.UserVO;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ShoppingListItemService {
    /**
     * Method returns shopping list, available for tracking for specific language.
     *
     * @param language needed language code
     * @return List of {@link ShoppingListItemDto}.
     */
    List<ShoppingListItemDto> findAll(String language);

    /**
     * Method for saving shopping list item from {@link ShoppingListItemPostDto}.
     *
     * @param shoppingListItemPostDto needed text
     * @author Dmytro Khonko
     */
    List<LanguageTranslationDTO> saveShoppingListItem(ShoppingListItemPostDto shoppingListItemPostDto);

    /**
     * Method to update shopping list item translations from
     * {@link ShoppingListItemPostDto}.
     *
     * @param shoppingListItemPostDto new text
     * @author Dmytro Khonko
     */
    List<LanguageTranslationDTO> update(ShoppingListItemPostDto shoppingListItemPostDto);

    /**
     * Method delete shopping list item.
     *
     * @param id id of shopping list item you need to delete
     * @author Dmytro Khonko
     */
    Long delete(Long id);

    /**
     * Method to find shopping list items.
     *
     * @param pageable our page
     * @author Dmytro Khonko
     */
    PageableAdvancedDto<ShoppingListItemManagementDto> findShoppingListItemsForManagementByPage(Pageable pageable);

    /**
     * Method search shopping list items.
     *
     * @param paging our page.
     * @param query  search request
     * @author Dmytro Khonko
     */
    PageableAdvancedDto<ShoppingListItemManagementDto> searchBy(Pageable paging, String query);

    /**
     * Method delete few shopping list items.
     *
     * @param listId ids of shopping list items you need to delete
     * @author Dmytro Khonko
     */
    List<Long> deleteAllShoppingListItemsByListOfId(List<Long> listId);

    /**
     * Method to find shopping list item by id.
     *
     * @param id id of shopping list item you need to find
     * @author Dmytro Khonko
     */
    ShoppingListItemResponseDto findShoppingListItemById(Long id);

    /**
     * Method to filter shopping list items.
     *
     * @param dto data of shopping list item you need to find
     * @author Dmytro Khonko
     */
    PageableAdvancedDto<ShoppingListItemManagementDto> getFilteredDataForManagementByPage(Pageable pageable,
        ShoppingListItemViewDto dto);

    /**
     * Method assign to user list of user shopping list items available for habit.
     *
     * @param userId   id of the {@link UserVO} current user.
     * @param language needed language code.
     * @param habitId  id of the {@link HabitVO}.
     * @return List of saved {@link UserShoppingListItemDto} with specific language.
     */
    List<UserShoppingListItemResponseDto> saveUserShoppingListItems(Long userId, Long habitId,
        List<ShoppingListItemRequestDto> dto, String language);

    /**
     * Method returns list of user shopping list for specific language.
     *
     * @param userId   id of the {@link UserVO} current user.
     * @param language needed language code.
     * @return List of {@link UserShoppingListItemDto}.
     */
    List<UserShoppingListItemResponseDto> getUserShoppingList(Long userId, Long habitId, String language);

    /**
     * Method returns list of user shopping list items by habitAssignId, specific
     * language and INPROGRESS status.
     *
     * @param habitAssignId id of the {@link Long} current user.
     * @param language      needed language code.
     * @return List of {@link UserShoppingListItemResponseDto}.
     */
    List<UserShoppingListItemResponseDto> getUserShoppingListItemsByHabitAssignIdAndStatusInProgress(
        Long habitAssignId, String language);

    /**
     * Method returns user shopping list by habitAssignId for specific language.
     *
     * @param userId        id of the {@link UserVO} current user.
     * @param habitAssignId {@link greencity.dto.habit.HabitAssignVO} id.
     * @param language      needed language code.
     * @return List of {@link UserShoppingListItemResponseDto}.
     */
    List<UserShoppingListItemResponseDto> getUserShoppingListByHabitAssignId(Long userId, Long habitAssignId,
        String language);

    /**
     * Method for deleting shopping list item from user`s shopping list.
     *
     * @param userId  id of the {@link UserVO} current user.
     * @param habitId id of the {@link HabitVO}.
     * @param itemId  id of the {@link ShoppingListItemVO}.
     */
    void deleteUserShoppingListItemByItemIdAndUserIdAndHabitId(Long itemId, Long userId, Long habitId);

    /**
     * Method update status of user shopping list item to done.
     *
     * @param userId   id of the {@link UserVO} current user.
     * @param itemId   - {@link UserShoppingListItemVO}'s id that should be updated.
     * @param language needed language code.
     * @return {@link UserShoppingListItemDto} with specific language.
     */
    UserShoppingListItemResponseDto updateUserShopingListItemStatus(Long userId, Long itemId, String language);

    /**
     * Method update status of user shopping list item to do.
     *
     * @param userId                 id of the {@link UserVO} current user.
     * @param userShoppingListItemId - {@link UserShoppingListItemVO}'s id that
     *                               should be updated.
     * @param language               needed language code.
     * @param status                 needed language code.
     * @return {@link UserShoppingListItemDto} with specific language.
     */
    List<UserShoppingListItemResponseDto> updateUserShoppingListItemStatus(Long userId,
        Long userShoppingListItemId,
        String language,
        String status);

    /**
     * Method for deleted list of user shopping list items.
     *
     * @param ids string with ids object for deleting.
     * @return list ids of deleted
     * @author Bogdan Kuzenko
     */
    List<Long> deleteUserShoppingListItems(String ids);

    /**
     * Method returns list of hopping list items for habit.
     *
     * @param habitId id of the {@link HabitVO}.
     * @return List of {@link ShoppingListItemManagementDto}.
     * @author Marian Diakiv
     */
    List<ShoppingListItemManagementDto> getShoppingListByHabitId(Long habitId);

    /**
     * Method returns shopping list items that are not in the habit.
     *
     * @param habitId  id of the {@link HabitVO}.
     * @param pageable - instance of {@link Pageable}.
     * @return Pageable of {@link ShoppingListItemManagementDto}.
     * @author Marian Diakiv
     */
    PageableAdvancedDto<ShoppingListItemManagementDto> findAllShoppingListItemsForManagementPageNotContained(
        Long habitId, Pageable pageable);

    /**
     * Method returns user's shopping list for active items and habits in progress.
     *
     * @param userId id of the {@link Long} current user
     * @param code   language code {@link String}
     * @return {@link ShoppingListItemDto}
     */
    List<ShoppingListItemDto> findInProgressByUserIdAndLanguageCode(Long userId, String code);
}
