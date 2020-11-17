package greencity.service;

import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.entity.HabitAssign;
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
    public HabitStatusCalendarVO findHabitStatusCalendarByEnrollDateAndHabitAssign(LocalDate date,
        HabitAssignVO habitAssignVO) {
        HabitAssign toFind = modelMapper.map(habitAssignVO, HabitAssign.class);
        HabitStatusCalendar calendar =
            habitStatusCalendarRepo.findHabitStatusCalendarByEnrollDateAndHabitAssign(date, toFind);
        return calendar == null ? null : modelMapper.map(calendar, HabitStatusCalendarVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitStatusCalendarVO save(HabitStatusCalendarVO habitStatusCalendarVO) {
        HabitStatusCalendar habitStatusCalendar = modelMapper.map(habitStatusCalendarVO, HabitStatusCalendar.class);
        return modelMapper.map(habitStatusCalendarRepo.save(habitStatusCalendar), HabitStatusCalendarVO.class);
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
    public LocalDate findTopByEnrollDateAndHabitAssign(HabitAssignVO habitAssignVO) {
        HabitAssign toFind = modelMapper.map(habitAssignVO, HabitAssign.class);
        return habitStatusCalendarRepo.findTopByEnrollDateAndHabitAssign(toFind);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LocalDate> findEnrolledDatesAfter(LocalDate dateTime, HabitAssignVO habitAssignVO) {
        HabitAssign toFind = modelMapper.map(habitAssignVO, HabitAssign.class);
        List<HabitStatusCalendar> habitStatusCalendars =
            habitStatusCalendarRepo.findAllByEnrollDateAfterAndHabitAssign(dateTime, toFind);
        List<LocalDate> dates = new LinkedList<>();
        habitStatusCalendars.forEach(habitStatusCalendar -> dates.add(habitStatusCalendar.getEnrollDate()));
        return dates;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LocalDate> findEnrolledDatesBefore(LocalDate dateTime, HabitAssignVO habitAssignVO) {
        HabitAssign toFind = modelMapper.map(habitAssignVO, HabitAssign.class);
        List<HabitStatusCalendar> habitStatusCalendars =
            habitStatusCalendarRepo.findAllByEnrollDateBeforeAndHabitAssign(dateTime, toFind);
        List<LocalDate> dates = new LinkedList<>();
        habitStatusCalendars.forEach(habitStatusCalendar -> dates.add(habitStatusCalendar.getEnrollDate()));
        return dates;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAllByHabitAssign(HabitAssignVO habitAssignVO) {
        habitStatusCalendarRepo.deleteAllByHabitAssign(modelMapper.map(habitAssignVO, HabitAssign.class));
    }
}
