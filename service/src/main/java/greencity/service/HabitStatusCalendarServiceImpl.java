package greencity.service;

import greencity.dto.habitstatus.HabitStatusDto;
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
    public void save(HabitStatusCalendarVO habitStatusCalendar) {
        habitStatusCalendarRepo.save(modelMapper.map(habitStatusCalendar, HabitStatusCalendar.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitStatusCalendarVO findHabitStatusCalendarByEnrollDateAndHabitStatus(LocalDate date,
                                                                                 HabitStatusDto habitStatusDto) {
        HabitStatus toFind = modelMapper.map(habitStatusDto, HabitStatus.class);
        HabitStatusCalendar calendar =
            habitStatusCalendarRepo.findHabitStatusCalendarByEnrollDateAndHabitStatus(date, toFind);
        return modelMapper.map(calendar, HabitStatusCalendarVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(HabitStatusCalendarVO habitStatusCalendar) {
        habitStatusCalendarRepo.delete(modelMapper.map(habitStatusCalendar, HabitStatusCalendar.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate findTopByEnrollDateAndHabitStatus(HabitStatusDto habitStatusDto) {
        HabitStatus toFind = modelMapper.map(habitStatusDto, HabitStatus.class);
        return habitStatusCalendarRepo.findTopByEnrollDateAndHabitStatus(toFind);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LocalDate> findEnrolledDatesAfter(LocalDate dateTime, HabitStatusDto habitStatusDto) {
        HabitStatus toFind = modelMapper.map(habitStatusDto, HabitStatus.class);
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
    public List<LocalDate> findEnrolledDatesBefore(LocalDate dateTime, HabitStatusDto habitStatusDto) {
        HabitStatus toFind = modelMapper.map(habitStatusDto, HabitStatus.class);
        List<HabitStatusCalendar> habitStatusCalendars =
            habitStatusCalendarRepo.findAllByEnrollDateBeforeAndHabitStatus(dateTime, toFind);
        List<LocalDate> dates = new LinkedList<>();
        habitStatusCalendars.forEach(habitStatusCalendar -> dates.add(habitStatusCalendar.getEnrollDate()));

        return dates;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void deleteAllByHabitStatus(HabitStatusDto habitStatus) {
        habitStatusCalendarRepo.deleteAllByHabitStatusId(habitStatus.getId());
    }
}
