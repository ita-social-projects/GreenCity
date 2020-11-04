package greencity.service;

import greencity.dto.habitstatus.HabitStatusVO;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface HabitStatusCalendarService {
    /**
     * Method to find {@link HabitStatusCalendarVO} by {@link LocalDate} and {@code HabitStatus}.
     *
     * @param date        after this date the search is performed.
     * @param habitStatus target {@link HabitStatusVO}.
     * @return {@link HabitStatusCalendarVO}.
     */
    HabitStatusCalendarVO findHabitStatusCalendarByEnrollDateAndHabitStatus(LocalDate date, HabitStatusVO habitStatus);

    /**
     * Method to save {@link HabitStatusCalendarVO}.
     *
     * @param habitStatusCalendar {@link HabitStatusCalendarVO} which will be saved.
     */
    void save(HabitStatusCalendarVO habitStatusCalendar);

    /**
     * Method to delete {@link HabitStatusCalendarVO}.
     *
     * @param habitStatusCalendar {@link HabitStatusCalendarVO} which will be deleted.
     */
    void delete(HabitStatusCalendarVO habitStatusCalendar);

    /**
     * Method to return the latest EnrollDate of {@code HabitStatus}.
     *
     * @param habitStatus {@link HabitStatusVO} instance.
     * @return {@link LocalDateTime}.
     */
    LocalDate findTopByEnrollDateAndHabitStatus(HabitStatusVO habitStatus);

    /**
     * Method to return all enrolled {@link HabitStatusVO} dates after {@link LocalDateTime} dateTime.
     *
     * @param dateTime    after this {@link LocalDateTime} the search is performed.
     * @param habitStatus target {@link HabitStatusVO}.
     * @return {@link List} of {@link HabitStatusCalendarVO} dates.
     */
    List<LocalDate> findEnrolledDatesAfter(LocalDate dateTime, HabitStatusVO habitStatus);

    /**
     * Method return all enrolled {@link HabitStatusVO} dates before {@link LocalDateTime} dateTime.
     *
     * @param dateTime    after this date the search is performed.
     * @param habitStatus target {@link HabitStatusVO}.
     * @return {@link List} of {@link HabitStatusCalendarVO} dates.
     */
    List<LocalDate> findEnrolledDatesBefore(LocalDate dateTime, HabitStatusVO habitStatus);

    /**
     * Method deletes all {@code HabitStatusCalendar} by {@link HabitStatusVO} instance.
     *
     * @param habitStatusVO {@link HabitStatusVO} instance.
     */
    void deleteAllByHabitStatus(HabitStatusVO habitStatusVO);
}
