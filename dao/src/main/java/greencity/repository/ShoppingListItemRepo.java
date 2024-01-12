package greencity.repository;

import greencity.entity.ShoppingListItem;
import java.util.List;
import greencity.entity.localization.ShoppingListItemTranslation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingListItemRepo
    extends JpaRepository<ShoppingListItem, Long>, JpaSpecificationExecutor<ShoppingListItem> {
    /**
     * Method returns {@link ShoppingListItem} by search query and page.
     *
     * @param paging {@link Pageable}.
     * @param query  query to search.
     * @return list of {@link ShoppingListItem}.
     */
    @Query("SELECT g FROM ShoppingListItem g join g.translations as gt"
        + " WHERE CONCAT(g.id,'') LIKE LOWER(CONCAT('%', :query, '%')) "
        + "OR LOWER(gt.language.code) LIKE LOWER(CONCAT('%', :query, '%'))"
        + "OR LOWER(gt.content) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<ShoppingListItem> searchBy(Pageable paging, String query);

    /**
     * Method returns ShoppingList id which are not in the habit.
     *
     * @param habitId habit id
     * @return list of id.
     */
    @Query(nativeQuery = true,
        value = "select shopping_list_items.id from shopping_list_items  where id not in"
            + " (select shopping_list_item_id from habit_shopping_list_items where habit_id = :habitId and "
            + "habit_shopping_list_items.status like 'ACTUAL');")
    List<Long> getAllShoppingListItemsByHabitIdNotContained(@Param("habitId") Long habitId);

    /**
     * Method returns shopping list items id which are in the habit.
     *
     * @param habitId habit id
     * @return list of id.
     */
    @Query(nativeQuery = true,
        value = "select shopping_list_item_id from habit_shopping_list_items  where habit_id = :habitId and "
            + " habit_shopping_list_items.status like 'ACTUAL';")
    List<Long> getAllShoppingListItemIdByHabitIdISContained(@Param("habitId") Long habitId);

    /**
     * Method returns {@link ShoppingListItem} by list item id and pageable.
     *
     * @param listId habit id
     * @return list of {@link ShoppingListItem}.
     */
    @Query("select g from ShoppingListItem g where g.id in(:listId)")
    Page<ShoppingListItem> getShoppingListByListOfIdPageable(List<Long> listId, Pageable pageable);

    /**
     * Method returns {@link ShoppingListItem} by list item id.
     *
     * @param listId habit id
     * @return list of {@link ShoppingListItem}.
     */
    @Query("select g from ShoppingListItem g where g.id in( :listId )")
    List<ShoppingListItem> getShoppingListByListOfId(List<Long> listId);

    /**
     * Method returns user's shopping list for active items and habits in progress.
     *
     * @param userId id of the {@link Long} current user
     * @param code   language code {@link String}
     * @return {@link ShoppingListItemTranslation}
     */
    @Query("""
        select translations from UserShoppingListItem as usli\s
        join HabitAssign as ha on ha.id = usli.habitAssign.id
        join ShoppingListItemTranslation as translations on
        translations.shoppingListItem.id = usli.shoppingListItem.id
        join Language as lang on translations.language.id = lang.id
        where usli.status = 'INPROGRESS'
        and ha.status = 'INPROGRESS'
        and ha.user.id = :userId
        and lang.code = :code""")
    List<ShoppingListItemTranslation> findInProgressByUserIdAndLanguageCode(@Param("userId") Long userId,
        @Param("code") String code);

    /**
     * Method returns {@link ShoppingListItem} by habitId, list of name and language
     * code.
     *
     * @param habitId      habit id
     * @param itemNames    list of shopping items name
     * @param languageCode language code
     * @return list of {@link ShoppingListItem}
     */
    @Query("SELECT sli FROM ShoppingListItem sli "
        + "JOIN ShoppingListItemTranslation slt ON sli.id = slt.shoppingListItem.id "
        + "JOIN sli.habits h ON h.id = :habitId"
        + " WHERE slt.language.code = :languageCode AND slt.content in :listOfName")
    List<ShoppingListItem> findByNames(@Param("habitId") Long habitId, @Param("listOfName") List<String> itemNames,
        String languageCode);
}
