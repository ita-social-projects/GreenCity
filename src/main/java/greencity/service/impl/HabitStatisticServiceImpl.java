package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.habitstatistic.*;
import greencity.entity.Habit;
import greencity.entity.HabitStatistic;
import greencity.entity.enums.HabitRate;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.mapping.HabitStatisticMapper;
import greencity.repository.HabitRepo;
import greencity.repository.HabitStatisticRepo;
import greencity.service.HabitStatisticService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class HabitStatisticServiceImpl implements HabitStatisticService {
    private static final Integer DAY_DIFFERENCE = 1;
    private HabitStatisticRepo habitStatisticRepo;
    private HabitRepo habitRepo;
    private HabitStatisticMapper habitStatisticMapper;
    private ModelMapper modelMapper;


    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi
     */
    @Transactional
    @Override
    public AddHabitStatisticDto save(AddHabitStatisticDto dto) {
        if (habitStatisticRepo.findHabitStatByDate(dto.getCreatedOn(), dto.getHabitId()).isPresent()) {
            throw new NotSavedException(ErrorMessage.HABIT_STATISTIC_ALREADY_EXISTS);
        }
        if (checkDate(dto.getCreatedOn())) {
            HabitStatistic habitStatistic = habitStatisticMapper.convertToEntity(dto);

            return habitStatisticMapper.convertToDto(habitStatisticRepo.save(habitStatistic));
        } else {
            throw new BadRequestException(ErrorMessage.WRONG_DATE);
        }
    }

    private boolean checkDate(LocalDate date) {
        return LocalDate.now().compareTo(date) == 0
            || LocalDate.now().compareTo(date) == DAY_DIFFERENCE;
    }


    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi
     */
    @Transactional
    @Override
    public UpdateHabitStatisticDto update(Long habitStatisticId, UpdateHabitStatisticDto dto) {
        HabitStatistic updatable = findById(habitStatisticId);

        updatable.setAmountOfItems(dto.getAmountOfItems());
        updatable.setHabitRate(dto.getHabitRate());
        return modelMapper.map(habitStatisticRepo.save(updatable),
            UpdateHabitStatisticDto.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi
     */
    @Override
    public HabitStatistic findById(Long id) {
        return habitStatisticRepo
            .findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage
                .HABIT_STATISTIC_NOT_FOUND_BY_ID + id));
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi
     */
    @Override
    public List<Habit> findAllHabitsByUserId(Long userId) {
        return habitRepo.findAllByUserId(userId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_HAS_NOT_ANY_HABITS));
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi
     */
    @Override
    public List<Habit> findAllHabitsByStatus(Long userId, Boolean status) {
        List<Habit> habitList = findAllHabitsByUserId(userId)
            .stream()
            .filter(habit -> habit.getStatusHabit().equals(status))
            .collect(Collectors.toList());
        if (habitList.isEmpty()) {
            throw new NotFoundException(ErrorMessage.USER_HAS_NOT_HABITS_WITH_SUCH_STATUS + status);
        }
        return habitList;
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi
     */
    @Override
    public List<HabitDto> findAllHabitsAndTheirStatistics(Long id, Boolean status) {
        return findAllHabitsByStatus(id, status)
            .stream()
            .map(this::convertHabitToHabitDto)
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi
     */
    @Override
    public CalendarUsefulHabitsDto getInfoAboutUserHabits(Long userId) {
        List<Habit> allHabitsByUserId = findAllHabitsByStatus(userId, true);

        Map<String, Integer> statisticByHabitsPerMonth =
            getAmountOfUnTakenItemsPerMonth(allHabitsByUserId);

        Map<String, Integer> statisticUnTakenItemsWithPrevMonth =
            getDifferenceItemsWithPrevDay(allHabitsByUserId);

        CalendarUsefulHabitsDto dto = new CalendarUsefulHabitsDto();
        dto.setCreationDate(allHabitsByUserId.get(0).getCreateDate());
        dto.setAllItemsPerMonth(statisticByHabitsPerMonth);
        dto.setDifferenceUnTakenItemsWithPreviousDay(statisticUnTakenItemsWithPrevMonth);

        return dto;
    }

    private Integer getItemsForPreviousDay(Long habitId) {
        return habitStatisticRepo.getAmountOfItemsInPreviousDay(habitId).orElse(0);
    }

    private Integer getItemsTakenToday(Long habitId) {
        return habitStatisticRepo.getAmountOfItemsToday(habitId).orElse(0);
    }

    private Map<String, Integer> getAmountOfUnTakenItemsPerMonth(List<Habit> allHabitsByUserId) {
        LocalDate firstDayOfMonth = LocalDate.now();
        return allHabitsByUserId
            .stream()
            .collect(Collectors
                .toMap(habit -> habit.getHabitDictionary().getName(),
                    habit -> habitStatisticRepo
                        .getSumOfAllItemsPerMonth(habit.getId(), firstDayOfMonth.withDayOfMonth(1)).orElse(0)
                ));
    }

    private Map<String, Integer> getDifferenceItemsWithPrevDay(List<Habit> allHabitsByUserId) {
        return allHabitsByUserId
            .stream()
            .collect(Collectors
                .toMap(habit -> habit.getHabitDictionary().getName(),
                    habit -> getItemsTakenToday(habit.getId()) - getItemsForPreviousDay(habit.getId())
                ));
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkovskyi
     */
    @Override
    public List<HabitStatisticDto> findAllByHabitId(Long habitId) {
        return habitStatisticRepo.findAllByHabitId(habitId)
            .stream()
            .map(HabitStatisticDto::new)
            .collect(Collectors.toList());
    }

    private HabitDto convertHabitToHabitDto(Habit habit) {
        List<HabitStatisticDto> result = new ArrayList<>();
        List<HabitStatistic> habitStatistics = habit.getHabitStatistics();
        LocalDate localDate = habit.getCreateDate();
        int counter = 0;
        for (int i = 0; i < 21; i++) {
            if (counter < habitStatistics.size() && localDate.equals(habitStatistics.get(counter).getCreatedOn())) {
                result.add(new HabitStatisticDto(habit.getHabitStatistics().get(counter)));
                counter++;
            } else {
                result.add(new HabitStatisticDto(null, HabitRate.DEFAULT, localDate, 0));
            }
            localDate = localDate.plusDays(1);
        }
        return new HabitDto(habit.getId(), habit.getHabitDictionary().getName(),
            habit.getHabitDictionary().getHabitItem(), habit.getCreateDate(), result);
    }
}