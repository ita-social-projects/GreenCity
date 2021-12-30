package greencity.repository;

import greencity.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Provides an interface to manage {@link Habit} entity.
 */
@Repository
public interface HabitRepo extends JpaRepository<Habit, Long>, JpaSpecificationExecutor<Habit> {
    /**
     * Method add goal to habit by id and status ACTIVE. This method use native SQL
     * query.
     *
     * @param habitID Id of Habit
     * @param itemID  Id of ShoppingListItem
     * @author Marian Diakiv
     */
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "insert into habit_shopping_list_items(habit_id,shopping_list_item_id)"
        + "values (:habitID,:itemID);")
    void addShopingListItemToHabit(@Param("habitID") Long habitID, @Param("itemID") Long itemID);

    /**
     * Method to change status. This method use native SQL query.
     *
     * @param habitID Id of Habit
     * @param itemID  Id of Shopping list item
     * @author Marian Diakiv
     */
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update habit_shopping_list_items set status = 'DELETED'"
        + " where habit_shopping_list_items.habit_id = :habitID and "
        + "habit_shopping_list_items.shopping_list_item_id = :itemID"
        + " and habit_shopping_list_items.status like 'ACTUAL'")
    void upadateShopingListItemInHabit(@Param("habitID") Long habitID, @Param("itemID") Long itemID);
}
