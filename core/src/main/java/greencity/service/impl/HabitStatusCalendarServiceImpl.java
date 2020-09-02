package greencity.service.impl;

import greencity.entity.HabitStatus;
import greencity.entity.HabitStatusCalendar;
import greencity.repository.HabitStatusCalendarRepo;
import greencity.service.HabitStatusCalendarService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HabitStatusCalendarServiceImpl implements HabitStatusCalendarService {
    private HabitStatusCalendarRepo habitStatusCalendarRepo;

    /**
     * Method save {@link HabitStatusCalendar}.
     *
     * @param habitStatusCalendar - {@link HabitStatusCalendar} which will be saved
     */
    @Override
    public void save(HabitStatusCalendar habitStatusCalendar) {
        habitStatusCalendarRepo.save(habitStatusCalendar);
    }

    /**
     * Method find {@link HabitStatusCalendar} by date and {@link HabitStatus}.
     *
     * @param date        - after this date the search is performed
     * @param habitStatus - target {@link HabitStatus}
     * @return {@link HabitStatusCalendar}
     */
    @Override
    public HabitStatusCalendar findHabitStatusCalendarByEnrollDateAndHabitStatus(LocalDate date,
                                                                                 HabitStatus habitStatus) {
        return habitStatusCalendarRepo.findHabitStatusCalendarByEnrollDateAndHabitStatus(date, habitStatus);
    }

    /**
     * Method delete {@link HabitStatusCalendar}.
     *
     * @param habitStatusCalendar - {@link HabitStatusCalendar} which will be deleted
     */
    @Override
    public void delete(HabitStatusCalendar habitStatusCalendar) {
        habitStatusCalendarRepo.delete(habitStatusCalendar);
    }

    /**
     * Method return the latest EnrollDate of {@link HabitStatus}.
     *
     * @param habitStatus target {@link HabitStatus}
     * @return {@link LocalDateTime}
     */
    @Override
    public LocalDate findTopByEnrollDateAndHabitStatus(HabitStatus habitStatus) {
        return habitStatusCalendarRepo.findTopByEnrollDateAndHabitStatus(habitStatus);
    }

    /**
     * Method return all enrolled {@link HabitStatus} after dateTime.
     *
     * @param dateTime    after this date the search is performed
     * @param habitStatus target {@link HabitStatus}
     * @return {@link List} of {@link HabitStatusCalendar}
     */
    @Override
    public List<LocalDate> findEnrolledDatesAfter(LocalDate dateTime, HabitStatus habitStatus) {
        List<HabitStatusCalendar> habitStatusCalendars =
            habitStatusCalendarRepo.findAllByEnrollDateAfterAndHabitStatus(dateTime, habitStatus);
        List<LocalDate> dates = new LinkedList<>();
        habitStatusCalendars.forEach(habitStatusCalendar -> dates.add(habitStatusCalendar.getEnrollDate()));

        return dates;
    }

    /**
     * Method return all enrolled {@link HabitStatus} before dateTime.
     *
     * @param dateTime    after this date the search is performed
     * @param habitStatus target {@link HabitStatus}
     * @return {@link List} of {@link HabitStatusCalendar}
     */
    @Override
    public List<LocalDate> findEnrolledDatesBefore(LocalDate dateTime, HabitStatus habitStatus) {
        List<HabitStatusCalendar> habitStatusCalendars =
            habitStatusCalendarRepo.findAllByEnrollDateBeforeAndHabitStatus(dateTime, habitStatus);
        List<LocalDate> dates = new LinkedList<>();
        habitStatusCalendars.forEach(habitStatusCalendar -> dates.add(habitStatusCalendar.getEnrollDate()));

        return dates;
    }
}
