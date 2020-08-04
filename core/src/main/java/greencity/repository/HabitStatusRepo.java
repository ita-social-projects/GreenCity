package greencity.repository;

import greencity.entity.HabitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitStatusRepo extends JpaRepository<HabitStatus, Long> {

    /**
     * Method delete {@link HabitStatus} by userId
     * @param userId - id of {@link greencity.entity.User}
     */
    void deleteHabitStatusByUserId(Long userId);

    /**
     * Method return {@link HabitStatus} by habitId and userId
     * @param habitId - id of {@link greencity.entity.Habit}
     * @param userId - id of {@link greencity.entity.User}
     * @return {@link HabitStatus}
     */
    HabitStatus findByHabitIdAndUserId(Long habitId, Long userId);
}
