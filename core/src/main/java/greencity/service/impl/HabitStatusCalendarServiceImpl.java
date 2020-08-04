package greencity.service.impl;

import greencity.entity.HabitStatus;
import greencity.entity.HabitStatusCalendar;
import greencity.repository.HabitStatusCalendarRepo;
import greencity.service.HabitStatusCalendarService;
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
     * Method save {@link HabitStatusCalendar}
     * @param habitStatusCalendar - {@link HabitStatusCalendar} which will be saved
     */
    @Override
    public void save(HabitStatusCalendar habitStatusCalendar) {
        habitStatusCalendarRepo.save(habitStatusCalendar);
    }

    /**
     * Find {@link HabitStatusCalendar} by date interval and {@link HabitStatus}
     * @param startDate - beginning of the interval
     * @param endDate - ending of the interval
     * @return {@link HabitStatusCalendar}
     */
    @Override
    public HabitStatusCalendar findByEnrollDateIsBetweenAndHabitStatus(LocalDateTime startDate, LocalDateTime endDate,
                                                                       HabitStatus habitStatus) {
        return habitStatusCalendarRepo.findByEnrollDateIsBetweenAndHabitStatus(startDate, endDate, habitStatus);
    }

    /**
     * Method delete {@link HabitStatusCalendar}
     * @param habitStatusCalendar - {@link HabitStatusCalendar} which will be deleted
     */
    @Override
    public void delete(HabitStatusCalendar habitStatusCalendar) {
        habitStatusCalendarRepo.delete(habitStatusCalendar);
    }

    /**
     * Method return the latest EnrollDate of {@link HabitStatus}
     * @param habitStatus target {@link HabitStatus}
     * @return {@link LocalDateTime}
     */
    @Override
    public LocalDateTime findTopByEnrollDateAndHabitStatus(HabitStatus habitStatus) {
        return habitStatusCalendarRepo.findTopByEnrollDateAndHabitStatus(habitStatus);
    }

    /**
     * Method return all enrolled {@link HabitStatus} before dateTime
     * @param dateTime after this date the search is performed
     * @param habitStatus target {@link HabitStatus}
     * @return {@link List<HabitStatusCalendar>}
     */
    @Override
    public List<LocalDateTime> findEnrolledDatesAfter(LocalDateTime dateTime, HabitStatus habitStatus) {
        List<HabitStatusCalendar> habitStatusCalendars =
            habitStatusCalendarRepo.findAllByEnrollDateAfterAndHabitStatus(dateTime, habitStatus);
        List<LocalDateTime> dates = new LinkedList<>();
        habitStatusCalendars.forEach(habitStatusCalendar -> dates.add(habitStatusCalendar.getEnrollDate()));

        return dates;
    }

}
