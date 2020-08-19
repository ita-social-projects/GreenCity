package greencity.service;

import greencity.entity.HabitStatus;
import greencity.entity.HabitStatusCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface HabitStatusCalendarService {
    /**
     * Method find {@link HabitStatusCalendar} by date and {@link HabitStatus}.
     *
     * @param date        - after this date the search is performed
     * @param habitStatus - target {@link HabitStatus}
     * @return {@link HabitStatusCalendar}
     */
    HabitStatusCalendar findHabitStatusCalendarByEnrollDateAndHabitStatus(LocalDate date, HabitStatus habitStatus);

    /**
     * Method save {@link HabitStatusCalendar}.
     *
     * @param habitStatusCalendar - {@link HabitStatusCalendar} which will be saved
     */
    void save(HabitStatusCalendar habitStatusCalendar);

    /**
     * Method delete {@link HabitStatusCalendar}.
     *
     * @param habitStatusCalendar - {@link HabitStatusCalendar} which will be deleted
     */
    void delete(HabitStatusCalendar habitStatusCalendar);

    /**
     * Method return the latest EnrollDate of {@link HabitStatus}.
     *
     * @param habitStatus target {@link HabitStatus}
     * @return {@link LocalDateTime}
     */
    LocalDate findTopByEnrollDateAndHabitStatus(HabitStatus habitStatus);

    /**
     * Method return all enrolled {@link HabitStatus} after dateTime.
     *
     * @param dateTime    after this date the search is performed
     * @param habitStatus target {@link HabitStatus}
     * @return {@link List} of {@link HabitStatusCalendar}
     */
    List<LocalDate> findEnrolledDatesAfter(LocalDate dateTime, HabitStatus habitStatus);

    /**
     * Method return all enrolled {@link HabitStatus} before dateTime.
     *
     * @param dateTime    after this date the search is performed
     * @param habitStatus target {@link HabitStatus}
     * @return {@link List} of {@link HabitStatusCalendar}
     */
    List<LocalDate> findEnrolledDatesBefore(LocalDate dateTime, HabitStatus habitStatus);
}
