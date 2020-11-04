package greencity.service;

import greencity.dto.habitstatus.HabitStatusVO;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.entity.HabitStatus;
import greencity.entity.HabitStatusCalendar;
import greencity.repository.HabitStatusCalendarRepo;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HabitStatusCalendarServiceImpl implements HabitStatusCalendarService {
    private final HabitStatusCalendarRepo habitStatusCalendarRepo;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitStatusCalendarVO findHabitStatusCalendarByEnrollDateAndHabitStatus(LocalDate date,
                                                                                   HabitStatusVO habitStatusVO) {
        HabitStatus toFind = modelMapper.map(habitStatusVO, HabitStatus.class);
        HabitStatusCalendar calendar =
            habitStatusCalendarRepo.findHabitStatusCalendarByEnrollDateAndHabitStatus(date, toFind);
        return calendar == null ? null : modelMapper.map(calendar, HabitStatusCalendarVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(HabitStatusCalendarVO habitStatusCalendarVO) {
        HabitStatusCalendar habitStatusCalendar = modelMapper.map(habitStatusCalendarVO, HabitStatusCalendar.class);
        habitStatusCalendarRepo.save(habitStatusCalendar);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(HabitStatusCalendarVO habitStatusCalendarVO) {
        HabitStatusCalendar habitStatusCalendar = modelMapper.map(habitStatusCalendarVO, HabitStatusCalendar.class);
        habitStatusCalendarRepo.delete(habitStatusCalendar);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate findTopByEnrollDateAndHabitStatus(HabitStatusVO habitStatusVO) {
        HabitStatus toFind = modelMapper.map(habitStatusVO, HabitStatus.class);
        return habitStatusCalendarRepo.findTopByEnrollDateAndHabitStatus(toFind);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LocalDate> findEnrolledDatesAfter(LocalDate dateTime, HabitStatusVO habitStatusVO) {
        HabitStatus toFind = modelMapper.map(habitStatusVO, HabitStatus.class);
        List<HabitStatusCalendar> habitStatusCalendars =
            habitStatusCalendarRepo.findAllByEnrollDateAfterAndHabitStatus(dateTime, toFind);
        List<LocalDate> dates = new LinkedList<>();
        habitStatusCalendars.forEach(habitStatusCalendar -> dates.add(habitStatusCalendar.getEnrollDate()));

        return dates;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LocalDate> findEnrolledDatesBefore(LocalDate dateTime, HabitStatusVO habitStatusVO) {
        HabitStatus toFind = modelMapper.map(habitStatusVO, HabitStatus.class);
        List<HabitStatusCalendar> habitStatusCalendars =
            habitStatusCalendarRepo.findAllByEnrollDateBeforeAndHabitStatus(dateTime, toFind);
        List<LocalDate> dates = new LinkedList<>();
        habitStatusCalendars.forEach(habitStatusCalendar -> dates.add(habitStatusCalendar.getEnrollDate()));

        return dates;
    }
}
