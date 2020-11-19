package greencity.repository;

import greencity.entity.HabitAssign;
import greencity.entity.HabitStatusCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface HabitStatusCalendarRepo extends JpaRepository<HabitStatusCalendar, Long> {
    /**
     * Method to find {@link HabitStatusCalendar} by {@link LocalDate} and
     * {@link HabitAssign}.
     *
     * @param date        after this date the search is performed.
     * @param habitAssign target {@link HabitAssign}.
     * @return {@link HabitStatusCalendar}.
     */
    HabitStatusCalendar findHabitStatusCalendarByEnrollDateAndHabitAssign(LocalDate date, HabitAssign habitAssign);

    /**
     * Method to return the latest EnrollDate of {@link HabitAssign}.
     *
     * @param habitAssign {@link HabitAssign} instance.
     * @return {@link LocalDateTime}.
     */
    @Query("SELECT max(hsc.enrollDate) FROM HabitStatusCalendar hsc WHERE hsc.habitAssign = ?1")
    LocalDate findTopByEnrollDateAndHabitAssign(HabitAssign habitAssign);

    /**
     * Method return all enrolled {@link HabitAssign} after dateTime.
     *
     * @param dateTime    after this date the search is performed.
     * @param habitAssign target {@link HabitAssign}.
     * @return {@link List} of {@link HabitStatusCalendar}.
     */
    List<HabitStatusCalendar> findAllByEnrollDateAfterAndHabitAssign(LocalDate dateTime, HabitAssign habitAssign);

    /**
     * Method return all enrolled {@link HabitAssign} before dateTime.
     *
     * @param dateTime    after this date the search is performed.
     * @param habitAssign target {@link HabitAssign}.
     * @return {@link List} of {@link HabitStatusCalendar}.
     */
    List<HabitStatusCalendar> findAllByEnrollDateBeforeAndHabitAssign(LocalDate dateTime, HabitAssign habitAssign);

    /**
     * Method deletes all {@link HabitStatusCalendar} by {@link HabitAssign}
     * instance.
     *
     * @param habitAssign {@link HabitAssign} instance.
     */
    @Modifying
    @Query("DELETE FROM HabitStatusCalendar hsc WHERE hsc.habitAssign = :hs")
    void deleteAllByHabitAssign(@Param("hs") HabitAssign habitAssign);

    /**
     * Method deletes {@link HabitStatusCalendar} by it's instance.
     *
     * @param habitStatusCalendar {@link HabitStatusCalendar} instance.
     */
    @Modifying
    @Query("DELETE FROM HabitStatusCalendar hsc WHERE hsc = :hs")
    void delete(@Param("hs") @NonNull HabitStatusCalendar habitStatusCalendar);
}
