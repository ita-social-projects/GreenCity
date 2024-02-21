package greencity.repository;

import greencity.entity.CustomShoppingListItem;
import greencity.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides an interface to manage {@link CustomShoppingListItem} entity.
 */
@Repository
public interface CustomShoppingListItemRepo extends JpaRepository<CustomShoppingListItem, Long> {
    /**
     * Method returns list of available (not ACTIVE) custom shopping list items for
     * user.
     *
     * @param userId id of the {@link User} current user
     * @return list of {@link CustomShoppingListItem}
     */
    @Query("SELECT cg FROM CustomShoppingListItem cg WHERE "
        + "NOT cg.status='DISABLED' "
        + "AND cg.user.id=:userId "
        + "AND cg.habit.id=:habitId "
        + "ORDER BY cg.id")
    List<CustomShoppingListItem> findAllAvailableCustomShoppingListItemsForUserId(@Param("userId") Long userId,
        @Param("habitId") Long habitId);

    /**
     * Method returns list of custom shopping list items by userId and habitId and
     * INPROGRESS status.
     *
     * @param userId  id of the {@link User} current user
     * @param habitId id of the {@link Long} habit
     * @return list of {@link CustomShoppingListItem}
     */
    @Query("SELECT cg FROM CustomShoppingListItem cg WHERE "
        + " cg.status='INPROGRESS' "
        + "AND cg.user.id=:userId "
        + "AND cg.habit.id=:habitId "
        + "ORDER BY cg.id")
    List<CustomShoppingListItem> findAllCustomShoppingListItemsForUserIdAndHabitIdInProgress(
        @Param("userId") Long userId, @Param("habitId") Long habitId);

    /**
     * Method returns particular selected custom shopping list items for user.
     *
     * @param userId id of the {@link User} current user
     * @return {@link CustomShoppingListItem}
     */
    @Query("SELECT cg FROM CustomShoppingListItem cg WHERE"
        + " cg.user.id=:userId")
    CustomShoppingListItem findByUserId(@Param("userId") Long userId);

    /**
     * Method find all custom shopping list items by user.
     *
     * @param userId  {@link CustomShoppingListItem} id
     * @param habitId {@link CustomShoppingListItem} id
     * @return list of {@link CustomShoppingListItem}
     */
    List<CustomShoppingListItem> findAllByUserIdAndHabitId(Long userId, Long habitId);

    /**
     * Method returns particular selected custom shopping list items for user.
     *
     * @param userId id of the {@link User} current user
     * @param itemId item id {@link Long}
     * @return {@link CustomShoppingListItem}
     */
    @Query("SELECT cg FROM CustomShoppingListItem cg WHERE"
        + " cg.user.id=:userId and cg.id=:itemId")
    CustomShoppingListItem findByUserIdAndItemId(@Param("userId") Long userId, @Param("itemId") Long itemId);

    /**
     * Method returns custom shopping list items by status.
     *
     * @param userId id of the {@link User} current user
     * @param status item id {@link String}
     * @return {@link CustomShoppingListItem}
     */
    @Query(value = "SELECT * from custom_shopping_list_items where user_id = :userId and status = :stat",
        nativeQuery = true)
    List<CustomShoppingListItem> findAllByUserIdAndStatus(@Param(value = "userId") Long userId,
        @Param(value = "stat") String status);

    /**
     * Method returns all custom shopping list items.
     *
     * @param userId id of the {@link User} current user
     * @return {@link CustomShoppingListItem}
     */
    @Query(value = "SELECT * from custom_shopping_list_items where user_id = :userId", nativeQuery = true)
    List<CustomShoppingListItem> findAllByUserId(@Param(value = "userId") Long userId);

    /**
     * Method delete selected items from custom shopping list.
     *
     * @param habitId id of needed habit
     * @author Anton Bondar
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM custom_shopping_list_items WHERE habit_id =:habitId", nativeQuery = true)
    void deleteCustomShoppingListItemsByHabitId(Long habitId);
}
