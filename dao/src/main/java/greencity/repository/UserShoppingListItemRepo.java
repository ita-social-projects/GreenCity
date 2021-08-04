package greencity.repository;

import greencity.entity.ShoppingListItem;
import greencity.entity.UserShoppingListItem;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * Method delete selected item from users shopping list.
     *
     * @param habitAssignId id of needed habit assign
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM user_shopping_list usl "
        + "WHERE usl.habit_assign_id =:habitAssignId ")
    void deleteByShoppingListItemsByHabitAssignId(Long habitAssignId);

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

    /**
     * Method returns shopping list with statuses DONE.
     *
     * @param habitAssignId id of needed habit assign
     * @param status        status of needed items
     * @return List of {@link Long}
     */
    @Query(nativeQuery = true,
        value = "SELECT shopping_list_item_id FROM user_shopping_list WHERE habit_assign_id = :habitAssignId"
            + " AND status = :status")
    List<Long> getShoppingListItemsByHabitAssignIdAndStatus(Long habitAssignId, String status);

    /**
     * Method returns shopping list with statuses DONE.
     *
     * @param userId {@link Long} user id.
     * @param itemId {@link Long} custom shopping list item id.
     */
    @Query(nativeQuery = true, value = "select usl.id from user_shopping_list as usl\n"
        + "join habit_assign as ha on ha.id = habit_assign_id\n"
        + "where ha.user_id = :userId and shopping_list_item_id = :itemId")
    Optional<Long> getByUserAndItemId(Long userId, Long itemId);

    /**
     * Method returns {@link UserShoppingListItem} by shopping list item id and user
     * id.
     *
     * @param itemId {@link Long}
     * @param userId {@link Long}
     * @return {@link UserShoppingListItem}
     */
    @Query(nativeQuery = true, value = "SELECT * FROM user_shopping_list u "
        + "JOIN habit_assign ha ON ha.id = u.habit_assign_id "
        + "JOIN habit_shopping_list_items hs ON hs.shopping_list_item_id = :itemId "
        + "WHERE ha.user_id = :userId AND (ha.habit_id = hs.habit_id AND u.shopping_list_item_id = :itemId);")
    List<UserShoppingListItem> getAllByShoppingListItemIdANdUserId(@Param(value = "itemId") Long itemId,
        @Param(value = "userId") Long userId);

    /*
     * @Query(nativeQuery = true, value =
     * "SELECT usl.duration, usl.working_days, usl.status FROM user_shopping_list usl "
     * + "JOIN habit_assign ha ON ha.id = usl.habit_assign_id " +
     * "JOIN shopping_list_item sli ON sli.id = usl.shopping_list_item " + "JOIN  "
     */
}
