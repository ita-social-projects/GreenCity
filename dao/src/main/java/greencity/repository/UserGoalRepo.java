package greencity.repository;

import greencity.entity.Goal;
import greencity.entity.UserGoal;
import java.util.List;
import java.util.Optional;

import greencity.entity.localization.GoalTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserGoalRepo extends JpaRepository<UserGoal, Long> {
    /**
     * Method returns list of {@link UserGoal} for specific user.
     *
     * @param habitAssignId - id of habit assign.
     * @return list of {@link UserGoal}
     */
    @Query("SELECT ug FROM UserGoal ug where ug.habitAssign.id =?1")
    List<UserGoal> findAllByHabitAssingId(Long habitAssignId);

    /**
     * Method returns predefined goal as specific UserGoal.
     *
     * @param userGoalId - id of userGoal.
     * @return {@link Goal}
     */
    @Query("SELECT ug.goal FROM UserGoal ug WHERE ug.id = ?1")
    Optional<Goal> findGoalByUserGoalId(Long userGoalId);

    /**
     * Method delete selected goal from users shopping list.
     *
     * @param goalId        id of needed goal
     * @param habitAssignId id of needed habit assign
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM user_goals ug "
        + "WHERE ug.goal_id =:goalId AND ug.habit_assign_id =:habitAssignId ")
    void deleteByGoalIdAndHabitAssignId(Long goalId, Long habitAssignId);

    /**
     * Method returns shopping list ids for habit.
     *
     * @param id id of needed habit
     * @return List of {@link Long}
     */
    @Query(nativeQuery = true, value = "SELECT goal_id FROM habit_goals WHERE habit_id = :id")
    List<Long> getAllGoalsIdForHabit(Long id);

    /**
     * Method returns shopping list ids selected by user.
     *
     * @param id id of needed habit assign
     * @return List of {@link Long}
     */
    @Query(nativeQuery = true, value = "SELECT goal_id FROM user_goals WHERE habit_assign_id = :id")
    List<Long> getAllAssignedGoals(Long id);
}
