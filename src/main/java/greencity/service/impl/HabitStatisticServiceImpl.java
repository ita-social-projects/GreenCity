package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.habitstatistic.*;
import greencity.entity.Habit;
import greencity.entity.HabitStatistic;
import greencity.entity.User;
import greencity.exception.BadRequestException;
import greencity.exception.NotFoundException;
import greencity.mapping.HabitStatisticMapper;
import greencity.repository.HabitRepo;
import greencity.repository.HabitStatisticRepo;
import greencity.repository.UserRepo;
import greencity.service.HabitStatisticService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class HabitStatisticServiceImpl implements HabitStatisticService {
    private HabitStatisticRepo habitStatisticRepo;
    private UserRepo userRepo;
    private HabitRepo habitRepo;
    private HabitStatisticMapper habitStatisticMapper;
    private ModelMapper modelMapper;

    @Override
    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi
     */
    public AddHabitStatisticDto save(AddHabitStatisticDto dto) {
        if (checkDate(dto.getCreatedOn())) {
            HabitStatistic habitStatistic = habitStatisticMapper.convertToEntity(dto);

            return habitStatisticMapper.convertToDto(habitStatisticRepo.save(habitStatistic));
        } else {
            throw new BadRequestException(ErrorMessage.WRONG_DATE);
        }
    }

    private boolean checkDate(LocalDate date) {
        return LocalDate.now().compareTo(date) == 0
            || LocalDate.now().compareTo(date) == 1;
    }

    @Override
    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi
     */
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
     * Method for finding all habits by user email.
     *
     * @param email - user email.
     * @return list of user's habits.
     */
    public List<Habit> findAllHabitsByUserEmail(String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException(ErrorMessage
            .USER_NOT_FOUND_BY_EMAIL));
        return habitRepo.findAllByUserId(user.getId());
    }

    /**
     *dasdasfgsskfa.
     *
     * @param email dasdas.
     * @param status asdas.
     * @return
     */
    public List<HabitDto> findAllHabitsByStatus(String email, Boolean status) {
        return findAllHabitsByUserEmail(email)
            .stream()
            .filter(habit -> habit.getStatusHabit().equals(status))
            .map(habit -> new HabitDto(habit.getId(), habit.getHabitDictionary().getName(), habit.getCreateDate()))
            .collect(Collectors.toList());
    }

    /**
     * Method for finding general info about user habits.
     *
     * @return
     */
    public CalendarUsefulHabits findAllStatistic(String email) {
        List<Habit> allHabitsByUserId = findAllHabitsByUserEmail(email);

        Map<String, Integer> statisticByHabitsPerMonth =
            getAmountOfUnTakenItemsPerMonth(allHabitsByUserId);

        Map<String, Integer> statisticUnTakenItemsWithPrevMonth =
            getDifferenceItemsWithPrevMonth(allHabitsByUserId);

        CalendarUsefulHabits dto = new CalendarUsefulHabits();

        dto.setCreationDate(allHabitsByUserId.get(0).getCreateDate());
        dto.setAmountUnTakenItemsPerMonth(statisticByHabitsPerMonth);
        dto.setDifferenceUnTakenItemsWithPreviousMonth(statisticUnTakenItemsWithPrevMonth);

        return dto;
    }

    private Map<String, Integer> getAmountOfUnTakenItemsPerMonth(List<Habit> allHabitsByUserId) {
        return allHabitsByUserId
            .stream()
            .collect(Collectors
                .toMap(habit -> habit.getHabitDictionary().getName(),
                    habit -> habitStatisticRepo
                        .getSumOfAllItems(habit.getId()).orElse(0)
                ));
    }

    private Map<String, Integer> getDifferenceItemsWithPrevMonth(List<Habit> allHabitsByUserId) {
        return allHabitsByUserId
            .stream()
            .collect(Collectors
                .toMap(habit -> habit.getHabitDictionary().getName(),
                    habit -> habitStatisticRepo
                        .getDifferenceItemsWithPreviousMonth(habit.getId()).orElse(0)
                ));
    }

    /**
     * dasdas.
     *
     * @param id dsadas.
     * @return
     */
    public List<HabitStatisticDto> findAllByHabitId(Long id) {
        return habitStatisticRepo.findAllByHabitId(id)
            .stream()
            .map(ft -> new HabitStatisticDto(ft.getId(), ft.getHabitRate(), ft.getCreatedOn(), ft.getAmountOfItems()))
            .collect(Collectors.toList());
    }
}