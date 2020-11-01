package greencity.service;

import greencity.converters.DateService;
import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habit.HabitManagementDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.HabitItemsAmountStatisticDto;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatistic;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.exception.exceptions.WrongIdException;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitRepo;
import greencity.repository.HabitStatisticRepo;
import greencity.constant.CacheConstants;
import greencity.constant.ErrorMessage;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@EnableCaching
@AllArgsConstructor
public class HabitStatisticServiceImpl implements HabitStatisticService {
    private final HabitStatisticRepo habitStatisticRepo;
    private final HabitAssignRepo habitAssignRepo;
    private final HabitRepo habitRepo;
    private final DateService dateService;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi && Yurii Koval
     */
    @Transactional
    @CacheEvict(value = CacheConstants.HABIT_ITEM_STATISTIC_CACHE, allEntries = true)
    @Override
    public HabitStatisticDto save(AddHabitStatisticDto dto) {
        if (habitStatisticRepo.findHabitAssignStatByDate(dto.getCreateDate(), dto.getHabitAssignId()).isPresent()) {
            throw new NotSavedException(ErrorMessage.HABIT_STATISTIC_ALREADY_EXISTS);
        }

        boolean proceed = isTodayOrYesterday(
            dateService
                .convertToDatasourceTimezone(dto.getCreateDate())
                .toLocalDate()
        );
        if (proceed) {
            HabitStatistic habitStatistic = modelMapper.map(dto, HabitStatistic.class);
            HabitAssign habitAssign = habitAssignRepo.findById(dto.getHabitAssignId())
                .orElseThrow(
                    () -> new NotFoundException(ErrorMessage.HABIT_ASSIGN_NOT_FOUND_BY_ID + dto.getHabitAssignId()));
            habitStatistic.setHabitAssign(habitAssign);
            return modelMapper.map(habitStatisticRepo.save(habitStatistic), HabitStatisticDto.class);
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
    @CacheEvict(value = CacheConstants.HABIT_ITEM_STATISTIC_CACHE, allEntries = true)
    @Override
    public UpdateHabitStatisticDto update(Long habitStatisticId, UpdateHabitStatisticDto dto) {
        HabitStatistic updatable = habitStatisticRepo.findById(habitStatisticId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_STATISTIC_NOT_FOUND_BY_ID + habitStatisticId));

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
    public HabitStatisticDto findById(Long id) {
        return modelMapper.map(habitStatisticRepo
            .findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage
                .HABIT_STATISTIC_NOT_FOUND_BY_ID + id)), HabitStatisticDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HabitStatisticDto> findAllStatsByHabitAssignId(Long habitAssignId) {
        HabitAssign habitAssign = habitAssignRepo.findById(habitAssignId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_ASSIGN_NOT_FOUND_BY_ID + habitAssignId));
        return modelMapper.map(habitStatisticRepo.findAllByHabitAssignId(habitAssign.getId()),
            new TypeToken<List<HabitStatisticDto>>() {
            }.getType());
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkovskyi
     */
    @Override
    public List<HabitStatisticDto> findAllStatsByHabitId(Long habitId) {
        Habit habit = habitRepo.findById(habitId).orElseThrow(
            () -> new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + habitId));
        return modelMapper.map(habitStatisticRepo.findAllByHabitId(habit.getId()),
            new TypeToken<List<HabitStatisticDto>>() {
            }.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = CacheConstants.HABIT_ITEM_STATISTIC_CACHE, key = "#language")
    @Override
    public List<HabitItemsAmountStatisticDto> getTodayStatisticsForAllHabitItems(String language) {
        return habitStatisticRepo.getStatisticsForAllHabitItemsByDate(ZonedDateTime.now(), language).stream()
            .map(it ->
                HabitItemsAmountStatisticDto.builder()
                    .habitItem((String) it.get(0))
                    .notTakenItems((long) it.get(1))
                    .build()
            ).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getAmountOfHabitsInProgressByUserId(Long userId) {
        return habitStatisticRepo.getAmountOfHabitsInProgressByUserId(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getAmountOfAcquiredHabitsByUserId(Long userId) {
        return habitStatisticRepo.getAmountOfAcquiredHabitsByUserId(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAllStatsByHabitAssign(HabitAssignVO habitAssignVO) {
        habitStatisticRepo.findAllByHabitAssignId(habitAssignVO.getId())
            .forEach(habitStatisticRepo::delete);
    }
}
