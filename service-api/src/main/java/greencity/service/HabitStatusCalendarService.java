package greencity.service;

import greencity.dto.habitstatus.HabitStatusDto;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface HabitStatusCalendarService {
    /**
     * Method to find {@link HabitStatusCalendarVO} by {@link LocalDate} and {@link HabitStatusDto}.
     *
     * @param date        after this date the search is performed.
     * @param habitStatus target {@link HabitStatusDto}.
     * @return {@link HabitStatusCalendarVO}.
     */
    HabitStatusCalendarVO findHabitStatusCalendarByEnrollDateAndHabitStatus(LocalDate date, HabitStatusDto habitStatus);

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
     * Method to return the latest EnrollDate of {@link HabitStatusDto}.
     *
     * @param habitStatusDto {@link HabitStatusDto} instance.
     * @return {@link LocalDateTime}.
     */
    LocalDate findTopByEnrollDateAndHabitStatus(HabitStatusDto habitStatusDto);

    /**
     * Method to return all enrolled {@link HabitStatusDto} after {@link LocalDateTime} dateTime.
     *
     * @param dateTime    after this {@link LocalDateTime} the search is performed.
     * @param habitStatusDto target {@link HabitStatusDto}.
     * @return {@link List} of {@link HabitStatusCalendarVO}.
     */
    List<LocalDate> findEnrolledDatesAfter(LocalDate dateTime, HabitStatusDto habitStatusDto);

    /**
     * Method return all enrolled {@link HabitStatusDto} before dateTime.
     *
     * @param dateTime    after this date the search is performed.
     * @param habitStatusDto target {@link HabitStatusDto}.
     * @return {@link List} of {@link HabitStatusCalendarVO}.
     */
    List<LocalDate> findEnrolledDatesBefore(LocalDate dateTime, HabitStatusDto habitStatusDto);

    /**
     * Method to delete all {@link HabitStatusCalendarVO} by {@link HabitStatusDto} id.
     *
     * @param habitStatus {@link HabitStatusDto} instance.
     */
    void deleteAllByHabitStatus(HabitStatusDto habitStatus);
}
