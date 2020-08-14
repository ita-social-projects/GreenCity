package greencity.repository;

import greencity.entity.HabitStatus;
import greencity.entity.HabitStatusCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HabitStatusCalendarRepo extends JpaRepository<HabitStatusCalendar, Long> {
    /**
     * Method find {@link HabitStatusCalendar} by date and {@link HabitStatus}.
     *
     * @param date        - after this date the search is performed
     * @param habitStatus - target {@link HabitStatus}
     * @return {@link HabitStatusCalendar}
     */
    HabitStatusCalendar findHabitStatusCalendarByEnrollDateAndHabitStatus(LocalDate date, HabitStatus habitStatus);

    /**
     * Method return the latest EnrollDate of {@link HabitStatus}.
     *
     * @param habitStatus target {@link HabitStatus}
     * @return {@link LocalDateTime}
     */
    @Query("SELECT max(hsc.enrollDate) FROM HabitStatusCalendar hsc WHERE hsc.habitStatus = ?1")
    LocalDate findTopByEnrollDateAndHabitStatus(HabitStatus habitStatus);

    /**
     * Method return all enrolled {@link HabitStatus} after dateTime.
     *
     * @param dateTime    after this date the search is performed
     * @param habitStatus target {@link HabitStatus}
     * @return {@link List} of {@link HabitStatusCalendar}
     */
    List<HabitStatusCalendar> findAllByEnrollDateAfterAndHabitStatus(LocalDate dateTime, HabitStatus habitStatus);

    /**
     * Method return all enrolled {@link HabitStatus} before dateTime.
     *
     * @param dateTime    after this date the search is performed
     * @param habitStatus target {@link HabitStatus}
     * @return {@link List} of {@link HabitStatusCalendar}
     */
    List<HabitStatusCalendar> findAllByEnrollDateBeforeAndHabitStatus(LocalDate dateTime, HabitStatus habitStatus);
}
