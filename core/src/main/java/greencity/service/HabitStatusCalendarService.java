package greencity.service;

import greencity.entity.HabitStatus;
import greencity.entity.HabitStatusCalendar;
import java.time.LocalDateTime;
import java.util.List;

public interface HabitStatusCalendarService {

    /**
     * Find {@link HabitStatusCalendar} by date interval and {@link HabitStatus}
     * @param startDate - beginning of the interval
     * @param endDate - ending of the interval
     * @return {@link HabitStatusCalendar}
     */
    HabitStatusCalendar findByEnrollDateIsBetweenAndHabitStatus(LocalDateTime startDate, LocalDateTime endDate,
                                                                HabitStatus habitStatus);

    /**
     * Method save {@link HabitStatusCalendar}
     * @param habitStatusCalendar - {@link HabitStatusCalendar} which will be saved
     */
    void save(HabitStatusCalendar habitStatusCalendar);

    /**
     * Method delete {@link HabitStatusCalendar}
     * @param habitStatusCalendar - {@link HabitStatusCalendar} which will be deleted
     */
    void delete(HabitStatusCalendar habitStatusCalendar);

    /**
     * Method return the latest EnrollDate of {@link HabitStatus}
     * @param habitStatus target {@link HabitStatus}
     * @return {@link LocalDateTime}
     */
    LocalDateTime findTopByEnrollDateAndHabitStatus(HabitStatus habitStatus);

    /**
     * Method return all enrolled {@link HabitStatus} before dateTime
     * @param dateTime after this date the search is performed
     * @param habitStatus target {@link HabitStatus}
     * @return {@link List<HabitStatusCalendar>}
     */
    List<LocalDateTime> findEnrolledDatesAfter(LocalDateTime dateTime, HabitStatus habitStatus);

}
