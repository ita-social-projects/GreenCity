package greencity.repository;

import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

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

    /**
     * Method to find customHabit by id and isCustomHabit true.
     *
     * @param id - custom habit id
     * @return {@link Optional} of {@link Habit} instance if present by id
     *
     * @author Olena Sotnik
     */
    Optional<Habit> findByIdAndIsCustomHabitIsTrue(@Param("id") Long id);

    /**
     * Method to find habit assign of user who owns habit by userId and habitId.
     *
     * @param userId  {@link Long} userId of user who owns habit.
     * @param habitId {@link Long} habitId.
     * @return {@link List} of {@link HabitAssign} of current habit's owner.
     *
     * @author Olena Sotnik
     */
    @Query(value = "SELECT DISTINCT ha.id "
        + "FROM habit_assign AS ha "
        + "WHERE ha.habit_id =:habitId "
        + "AND ha.user_id =:userId", nativeQuery = true)
    List<Long> findHabitAssignByHabitIdAndHabitOwnerId(@Param("habitId") Long habitId, @Param("userId") Long userId);

    /**
     * Finds and returns a list of IDs for habits that are visible to a given user.
     * The returned habits include:
     * <ul>
     * <li>Habits owned by the user.</li>
     * <li>Public habits (those that do not have a tag with ID 25).</li>
     * <li>Private habits (those that have a tag with ID 25) that are assigned to
     * the user.</li>
     * <li>Private habits that are shared with the user's friends (using the
     * isSharedWithFriends flag).</li>
     * </ul>
     *
     * @param userId the ID of the user for whom visible habit IDs are to be
     *               retrieved.
     * @return a {@link List} of {@link Long} IDs of habits that are visible to thе
     *         specified user.
     */
    @Query(value = """
        SELECT DISTINCT h.id
        FROM habits h
        LEFT JOIN habits_tags ht ON h.id = ht.habit_id
        LEFT JOIN tags t ON ht.tag_id = t.id
        LEFT JOIN tag_translations tt ON tt.tag_id = t.id AND tt.language_id = 2 AND tt.name = 'Private'
        LEFT JOIN habit_assign ha ON h.id = ha.habit_id AND ha.user_id = :userId
        LEFT JOIN users_friends uf ON uf.status = 'FRIEND'
                                    AND ((uf.user_id = :userId AND uf.friend_id = h.user_id)
                                         OR (uf.user_id = h.user_id AND uf.friend_id = :userId))
        WHERE h.user_id = :userId
        OR tt.name IS NULL
        OR ha.user_id = :userId
        OR (h.is_shared_with_friends = TRUE AND uf.user_id IS NOT NULL)
        """, nativeQuery = true)
    List<Long> findVisibleCustomHabitsIdsByUserId(@Param("userId") Long userId);

    /**
     * Determines if a habit is private based on its tags. A habit is considered
     * private if it has a tag with ID 25.
     *
     * @param habitId the ID of the habit to check.
     * @return {@code true} if the habit is private, {@code false} otherwise.
     */
    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM habits_tags ht
                JOIN tag_translations tt ON ht.tag_id = tt.tag_id
                WHERE ht.habit_id = :habitId
                AND tt.language_id = 2
                AND tt.name = 'Private'
            )
        """, nativeQuery = true)
    boolean isHabitPrivate(@Param("habitId") Long habitId);

    /**
     * Checks if a user can assign a specific private habit.
     * This method verifies if the habit meets the following conditions:
     * <ul>
     * <li>The habit is owned by the user.</li>
     * <li>The habit is private (it has a tag with ID 25 and tag name
     * 'Private').</li>
     * <li>Either the habit is explicitly assigned to the user, or it is shared with
     * friends and the user is a friend of the habit's owner.</li>
     * </ul>
     *
     * @param userId  the ID of the user who is attempting to assign the habit.
     * @param habitId the ID of the habit to check.
     * @return {@code true} if the user can assign the habit; {@code false}
     *         otherwise.
     */
    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM habits h
            LEFT JOIN habits_tags ht ON ht.habit_id = h.id
            LEFT JOIN tag_translations tt ON tt.tag_id = ht.tag_id AND tt.language_id = 2 AND tt.name = 'Private'
            WHERE h.id = :habitId
            AND h.user_id = :userId
            AND tt.name IS NOT NULL
            AND (
                EXISTS (
                    SELECT 1
                    FROM habit_assign ha
                    WHERE ha.user_id = :userId
                    AND ha.habit_id = h.id
                )
                OR (
                    h.is_shared_with_friends = TRUE
                    AND EXISTS (
                        SELECT 1
                        FROM users_friends uf
                        WHERE uf.status = 'FRIEND'
                        AND ((uf.user_id = :userId AND uf.friend_id = h.user_id)
                            OR (uf.user_id = h.user_id AND uf.friend_id = :userId))
                    )
                )
            )
        )
        """, nativeQuery = true)
    boolean canAssignPrivateHabitByUserIdAndHabitId(
        @Param("userId") Long userId,
        @Param("habitId") Long habitId);
}
