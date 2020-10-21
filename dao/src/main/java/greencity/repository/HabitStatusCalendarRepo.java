package greencity.repository;

import greencity.entity.HabitStatus;
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
     * Method to find {@link HabitStatusCalendar} by {@link LocalDate} and {@link HabitStatus}.
     *
     * @param date        after this date the search is performed.
     * @param habitStatus target {@link HabitStatus}.
     * @return {@link HabitStatusCalendar}.
     */
    HabitStatusCalendar findHabitStatusCalendarByEnrollDateAndHabitStatus(LocalDate date, HabitStatus habitStatus);

    /**
     * Method to return the latest EnrollDate of {@link HabitStatus}.
     *
     * @param habitStatus {@link HabitStatus} instance.
     * @return {@link LocalDateTime}.
     */
    @Query("SELECT max(hsc.enrollDate) FROM HabitStatusCalendar hsc WHERE hsc.habitStatus = ?1")
    LocalDate findTopByEnrollDateAndHabitStatus(HabitStatus habitStatus);

    /**
     * Method return all enrolled {@link HabitStatus} after dateTime.
     *
     * @param dateTime    after this date the search is performed.
     * @param habitStatus target {@link HabitStatus}.
     * @return {@link List} of {@link HabitStatusCalendar}.
     */
    List<HabitStatusCalendar> findAllByEnrollDateAfterAndHabitStatus(LocalDate dateTime, HabitStatus habitStatus);

    /**
     * Method return all enrolled {@link HabitStatus} before dateTime.
     *
     * @param dateTime    after this date the search is performed.
     * @param habitStatus target {@link HabitStatus}.
     * @return {@link List} of {@link HabitStatusCalendar}.
     */
    List<HabitStatusCalendar> findAllByEnrollDateBeforeAndHabitStatus(LocalDate dateTime, HabitStatus habitStatus);

    /**
     * Method deletes all {@link HabitStatusCalendar} by {@link HabitStatus} instance.
     *
     * @param habitStatus {@link HabitStatus} instance.
     */
    @Modifying
    @Query("DELETE FROM HabitStatusCalendar hsc WHERE hsc.habitStatus = :hs")
    void deleteAllByHabitStatus(@Param("hs") HabitStatus habitStatus);

    /**
     * Method deletes {@link HabitStatusCalendar} by it's instance.
     *
     * @param habitStatusCalendar {@link HabitStatusCalendar} instance.
     */
    @Modifying
    @Query("DELETE FROM HabitStatusCalendar hsc WHERE hsc = :hs")
    void delete(@Param("hs") @NonNull HabitStatusCalendar habitStatusCalendar);
}
