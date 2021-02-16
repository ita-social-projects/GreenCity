package greencity.repository;

import greencity.entity.ShoppingListItem;
import greencity.entity.UserShoppingListItem;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserShoppingListItemRepo extends JpaRepository<UserShoppingListItem, Long> {
    /**
     * Method returns list of {@link UserShoppingListItem} for specific user.
     *
     * @param habitAssignId - id of habit assign.
     * @return list of {@link UserShoppingListItem}
     */
    @Query("SELECT ug FROM UserShoppingListItem ug where ug.habitAssign.id =?1")
    List<UserShoppingListItem> findAllByHabitAssingId(Long habitAssignId);

    /**
     * Method returns predefined item as specific {@link UserShoppingListItem}.
     *
     * @param userShoppingListItemId - id of userGoal.
     * @return {@link ShoppingListItem}
     */
    @Query("SELECT ug.shoppingListItem FROM UserShoppingListItem ug WHERE ug.id = ?1")
    Optional<ShoppingListItem> findShoppingListByUserShoppingListItemId(Long userShoppingListItemId);

    /**
     * Method delete selected item from users shopping list.
     *
     * @param shoppingListItemId id of needed goal
     * @param habitAssignId      id of needed habit assign
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM user_shopping_list_item ug "
        + "WHERE ug.shopping_list_item_id =:shoppingListItemId AND ug.habit_assign_id =:habitAssignId ")
    void deleteByShoppingListItemIdAndHabitAssignId(Long shoppingListItemId, Long habitAssignId);

    /**
     * Method returns shopping list ids for habit.
     *
     * @param id id of needed habit
     * @return List of {@link Long}
     */
    @Query(nativeQuery = true, value = "SELECT shopping_list_item_id FROM habit_shopping_list_items "
        + "WHERE habit_id = :id")
    List<Long> getShoppingListItemsIdForHabit(Long id);

    /**
     * Method returns shopping list ids selected by user.
     *
     * @param id id of needed habit assign
     * @return List of {@link Long}
     */
    @Query(nativeQuery = true,
        value = "SELECT shopping_list_item_id FROM user_shopping_list WHERE habit_assign_id = :id")
    List<Long> getAllAssignedShoppingListItems(Long id);
}
