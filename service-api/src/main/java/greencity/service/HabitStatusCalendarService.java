package greencity.service;

import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface HabitStatusCalendarService {
    /**
     * Method to find {@link HabitStatusCalendarVO} by {@link LocalDate} and
     * {@code HabitStatus}.
     *
     * @param date        after this date the search is performed.
     * @param habitAssign target {@link HabitAssignVO}.
     * @return {@link HabitStatusCalendarVO}.
     */
    HabitStatusCalendarVO findHabitStatusCalendarByEnrollDateAndHabitAssign(LocalDate date, HabitAssignVO habitAssign);

    /**
     * Method to save {@link HabitStatusCalendarVO}.
     *
     * @param habitStatusCalendar {@link HabitStatusCalendarVO} which will be saved.
     * @return {@link HabitStatusCalendarVO}.
     */
    HabitStatusCalendarVO save(HabitStatusCalendarVO habitStatusCalendar);

    /**
     * Method to delete {@link HabitStatusCalendarVO}.
     *
     * @param habitStatusCalendar {@link HabitStatusCalendarVO} which will be
     *                            deleted.
     */
    void delete(HabitStatusCalendarVO habitStatusCalendar);

    /**
     * Method to return the latest EnrollDate of {@link HabitAssignVO}.
     *
     * @param habitAssignVO {@link HabitAssignVO} instance.
     * @return {@link LocalDateTime}.
     */
    LocalDate findTopByEnrollDateAndHabitAssign(HabitAssignVO habitAssignVO);

    /**
     * Method to return all enrolled {@link HabitAssignVO} dates after
     * {@link LocalDateTime} dateTime.
     *
     * @param dateTime    after this {@link LocalDateTime} the search is performed.
     * @param habitAssign target {@link HabitAssignVO}.
     * @return {@link List} of {@link HabitStatusCalendarVO} dates.
     */
    List<LocalDate> findEnrolledDatesAfter(LocalDate dateTime, HabitAssignVO habitAssign);

    /**
     * Method return all enrolled {@link HabitAssignVO} dates before
     * {@link LocalDateTime} dateTime.
     *
     * @param dateTime    after this date the search is performed.
     * @param habitAssign target {@link HabitAssignVO}.
     * @return {@link List} of {@link HabitStatusCalendarVO} dates.
     */
    List<LocalDate> findEnrolledDatesBefore(LocalDate dateTime, HabitAssignVO habitAssign);

    /**
     * Method deletes all {@code HabitStatusCalendar} by {@link HabitAssignVO}
     * instance.
     *
     * @param habitAssign {@link HabitAssignVO} instance.
     */
    void deleteAllByHabitAssign(HabitAssignVO habitAssign);
}
