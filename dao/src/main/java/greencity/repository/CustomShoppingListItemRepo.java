package greencity.repository;

import greencity.entity.CustomShoppingListItem;
import greencity.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
        + " cg.status='ACTIVE' AND cg.user.id=:userId")
    List<CustomShoppingListItem> findAllAvailableCustomShoppingListItemsForUserId(@Param("userId") Long userId);

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
     * @param id {@link CustomShoppingListItem} id
     * @return list of {@link CustomShoppingListItem}
     */
    List<CustomShoppingListItem> findAllByUserId(Long id);

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
}
