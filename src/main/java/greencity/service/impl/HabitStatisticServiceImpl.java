package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.habitstatistic.*;
import greencity.dto.user.HabitDictionaryDto;
import greencity.dto.user.HabitLogItemDto;
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
import greencity.converters.DateService;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class HabitStatisticServiceImpl implements HabitStatisticService {
    private final HabitStatisticRepo habitStatisticRepo;
    private final HabitRepo habitRepo;
    private final HabitStatisticMapper habitStatisticMapper;
    private final ModelMapper modelMapper;
    private final DateService dateService;

    /**
     * Constructor with parameters.
     */
    @Autowired
    public HabitStatisticServiceImpl(HabitStatisticRepo habitStatisticRepo,
                                     HabitRepo habitRepo,
                                     HabitStatisticMapper habitStatisticMapper,
                                     ModelMapper modelMapper,
                                     DateService dateService) {
        this.habitStatisticRepo = habitStatisticRepo;
        this.habitRepo = habitRepo;
        this.habitStatisticMapper = habitStatisticMapper;
        this.modelMapper = modelMapper;
        this.dateService = dateService;
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi && Yurii Koval
     */
    @Transactional
    @Override
    public AddHabitStatisticDto save(AddHabitStatisticDto dto) {
        if (habitStatisticRepo.findHabitStatByDate(dto.getCreatedOn(), dto.getHabitId()).isPresent()) {
            throw new NotSavedException(ErrorMessage.HABIT_STATISTIC_ALREADY_EXISTS);
        }
        boolean proceed = isTodayOrYesterday(
            dateService
                .convertToDatasourceTimezone(dto.getCreatedOn())
                .toLocalDate()
        );
        if (proceed) {
            HabitStatistic habitStatistic = habitStatisticMapper.convertToEntity(dto);
            return habitStatisticMapper.convertToDto(habitStatisticRepo.save(habitStatistic));
        }
        throw new BadRequestException(ErrorMessage.WRONG_DATE);
    }

    private boolean isTodayOrYesterday(LocalDate date) {
        int diff = Period.between(LocalDate.now(), date).getDays();
        return diff == 0 || diff == -1;
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

        List<HabitLogItemDto> statisticByHabitsPerMonth = getAmountOfUnTakenItemsPerMonth(allHabitsByUserId);

        List<HabitLogItemDto> statisticUnTakenItemsWithPrevMonth =
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

    private List<HabitLogItemDto> getAmountOfUnTakenItemsPerMonth(List<Habit> allHabitsByUserId) {
        ZonedDateTime firstDayOfMonth = dateService.getDatasourceZonedDateTime();
        return allHabitsByUserId
            .stream()
            .map(habit -> new HabitLogItemDto(
                habit.getHabitDictionary().getHabitItem(),
                habitStatisticRepo
                    .getSumOfAllItemsPerMonth(habit.getId(),
                        firstDayOfMonth.withDayOfMonth(1)).orElse(0))).collect(Collectors.toList());
    }

    private List<HabitLogItemDto> getDifferenceItemsWithPrevDay(List<Habit> allHabitsByUserId) {
        return allHabitsByUserId
            .stream()
            .map(habit -> new HabitLogItemDto(
                habit.getHabitDictionary().getHabitItem(),
                getItemsTakenToday(habit.getId()) - getItemsForPreviousDay(habit.getId())
            )).collect(Collectors.toList());
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
        ZonedDateTime zonedDateTime = habit.getCreateDate();
        int counter = 0;

        habitStatistics.sort(Comparator.comparing(HabitStatistic::getCreatedOn));

        for (int i = 0; i < 21; i++) {
            if (counter < habitStatistics.size()
                && zonedDateTime.compareTo(habitStatistics.get(counter).getCreatedOn()) == 0)
            {
                result.add(new HabitStatisticDto(habit.getHabitStatistics().get(counter)));
                counter++;
            } else {
                result.add(new HabitStatisticDto(null, HabitRate.DEFAULT, zonedDateTime, 0));
            }
            zonedDateTime = zonedDateTime.plusDays(1);
        }
        return new HabitDto(habit.getId(),
            habit.getHabitDictionary().getName(),
            habit.getStatusHabit(),
            habit.getHabitDictionary().getDescription(),
            habit.getHabitDictionary().getName(),
            habit.getHabitDictionary().getHabitItem(),
            habit.getCreateDate(),
            result,
            modelMapper.map(habit.getHabitDictionary(), HabitDictionaryDto.class)
        );
    }
}