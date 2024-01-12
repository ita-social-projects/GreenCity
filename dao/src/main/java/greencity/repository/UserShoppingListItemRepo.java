package greencity.repository;

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
     * Method delete selected item from users shopping list.
     *
     * @param shoppingListItemId id of needed goal
     * @param habitAssignId      id of needed habit assign
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM user_shopping_list ug "
        + "WHERE ug.shopping_list_item_id =:shoppingListItemId AND ug.habit_assign_id =:habitAssignId ")
    void deleteByShoppingListItemIdAndHabitAssignId(Long shoppingListItemId, Long habitAssignId);

    /**
     * Method delete selected items from users shopping list.
     *
     * @param habitAssignId id of needed habit assign
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM user_shopping_list usl "
        + "WHERE usl.habit_assign_id =:habitAssignId ")
    void deleteShoppingListItemsByHabitAssignId(Long habitAssignId);

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
     * Method returns UserShoppingListItem list by habit assign id.
     *
     * @param id id of needed habit assign
     * @return List of {@link Long}
     */
    @Query(nativeQuery = true,
        value = "SELECT * FROM user_shopping_list WHERE habit_assign_id = :id")
    List<UserShoppingListItem> getAllAssignedShoppingListItemsFull(Long id);

    /**
     * Method returns user shopping list items by habitAssignId and INPROGRESS
     * status.
     *
     * @param habitAssignId id of needed habit assign
     * @return List of {@link UserShoppingListItem}
     */

    @Query("SELECT ug FROM UserShoppingListItem ug WHERE "
        + " ug.status='INPROGRESS' "
        + "AND ug.habitAssign.id=:habitAssignId "
        + "ORDER BY ug.id")
    List<UserShoppingListItem> findUserShoppingListItemsByHabitAssignIdAndStatusInProgress(
        @Param("habitAssignId") Long habitAssignId);

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
    @Query(nativeQuery = true, value = """
        select usl.id from user_shopping_list as usl
        join habit_assign as ha on ha.id = habit_assign_id
        where ha.user_id = :userId and shopping_list_item_id = :itemId""")
    Optional<Long> getByUserAndItemId(Long userId, Long itemId);

    /**
     * Method returns {@link UserShoppingListItem} by user shopping list item id and
     * user id.
     *
     * @param userShoppingListItemId {@link Long}
     * @param userId                 {@link Long}
     * @return {@link UserShoppingListItem}
     * @author Anton Bondar
     */
    @Query(value = "SELECT u FROM UserShoppingListItem u JOIN HabitAssign ha ON ha.id = u.habitAssign.id "
        + "WHERE ha.user.id =:userId AND u.id =:userShoppingListItemId")
    List<UserShoppingListItem> getAllByUserShoppingListIdAndUserId(
        @Param(value = "userShoppingListItemId") Long userShoppingListItemId,
        @Param(value = "userId") Long userId);

    /*
     * @Query(nativeQuery = true, value =
     * "SELECT usl.duration, usl.working_days, usl.status FROM user_shopping_list usl "
     * + "JOIN habit_assign ha ON ha.id = usl.habit_assign_id " +
     * "JOIN shopping_list_item sli ON sli.id = usl.shopping_list_item " + "JOIN  "
     */
}
