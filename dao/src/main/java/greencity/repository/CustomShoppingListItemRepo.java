package greencity.repository;

import greencity.entity.CustomShoppingListItem;
import greencity.entity.User;

import greencity.entity.localization.ShoppingListItemTranslation;
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
        + " cg.status='ACTIVE' AND cg.user.id=:userId AND cg.habit.id=:habitId")
    List<CustomShoppingListItem> findAllAvailableCustomShoppingListItemsForUserId(@Param("userId") Long userId,
        @Param("habitId") Long habitId);

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
     * Method returns user's shopping list for active items and habits in progress.
     *
     * @param userId id of the {@link Long} current user
     * @param code   language code {@link String}
     * @return {@link ShoppingListItemTranslation}
     */
    @Query("select translations from UserShoppingListItem as usli \n"
        + "join HabitAssign as ha on ha.id = usli.habitAssign.id\n"
        + "join ShoppingListItemTranslation as translations on\n"
        + "translations.shoppingListItem.id = usli.shoppingListItem.id\n"
        + "join Language as lang on translations.language.id = lang.id\n"
        + "where usli.status = 'INPROGRESS'\n"
        + "and ha.status = 'INPROGRESS'\n"
        + "and ha.user.id = :userId\n"
        + "and lang.code = :code")
    List<ShoppingListItemTranslation> findInProgressByUserIdAndLanguageCode(@Param("userId") Long userId,
        @Param("code") String code);
}
