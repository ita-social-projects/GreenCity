package greencity.repository;

import greencity.entity.HabitStatus;
import greencity.entity.HabitStatusCalendar;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HabitStatusCalendarRepo extends JpaRepository<HabitStatusCalendar, Long> {

    /**
     * Method return {@link HabitStatusCalendar} by enrollDate between two dates and {@link HabitStatus}
     * @param startDate the date from which the interval begins
     * @param endDate interval end date
     * @param habitStatus target {@link HabitStatus}
     * @return {@link HabitStatusCalendar}
     */
    HabitStatusCalendar findByEnrollDateIsBetweenAndHabitStatus(LocalDateTime startDate, LocalDateTime endDate,
                                                                HabitStatus habitStatus);

    /**
     * Method return the latest EnrollDate of {@link HabitStatus}
     * @param habitStatus target {@link HabitStatus}
     * @return {@link LocalDateTime}
     */
    @Query("SELECT max(hsc.enrollDate) FROM HabitStatusCalendar hsc WHERE hsc.habitStatus = ?1")
    LocalDateTime findTopByEnrollDateAndHabitStatus(HabitStatus habitStatus);

    /**
     * Method return all enrolled {@link HabitStatus} before dateTime
     * @param dateTime after this date the search is performed
     * @param habitStatus target {@link HabitStatus}
     * @return {@link List<HabitStatusCalendar>}
     */
    List<HabitStatusCalendar> findAllByEnrollDateAfterAndHabitStatus(LocalDateTime dateTime, HabitStatus habitStatus);
}
