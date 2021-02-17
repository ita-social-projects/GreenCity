package greencity.repository;

import greencity.entity.ShoppingListItem;
import java.util.List;
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
}
