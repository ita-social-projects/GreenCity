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
     * Method to delete all {@link HabitStatusCalendar} by {@link HabitStatus} id.
     *
     * @param habitStatusId target {@link HabitStatus}.
     */
    @Modifying
    @Query(value = "DELETE FROM HabitStatusCalendar hsc WHERE hsc.habitStatus.id = :habitStatusId")
    void deleteAllByHabitStatusId(@Param("habitStatusId") Long habitStatusId);
}
