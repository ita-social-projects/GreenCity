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
import java.util.ArrayList;
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
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi
     */
    public List<Habit> findAllHabitsByUserEmail(String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException(ErrorMessage
            .USER_NOT_FOUND_BY_EMAIL));
        return habitRepo.findAllByUserId(user.getId())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_HAS_NOT_ANY_HABITS));
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi
     */
    public List<Habit> findAllHabitsByStatus(String email, Boolean status) {
        List<Habit> habitList = findAllHabitsByUserEmail(email)
            .stream()
            .filter(habit -> habit.getStatusHabit().equals(status))
            .collect(Collectors.toList());
        if (habitList.isEmpty()) {
            throw new NotFoundException(ErrorMessage.USER_HAS_NOT_HABITS_WITH_SUCH_STATUS + status);
        }
        return habitList;
    }

    /**
     * Method for finding all habits by status with.
     *
     * @param email  user's email.
     * @param status status of habit.
     * @return list of {@link HabitDto} instances.
     */
    public List<HabitDto> findAllHabits(String email, Boolean status) {
        return findAllHabitsByStatus(email, status)
            .stream()
            .map(this::convertHabitToHabitDto)
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi
     */
    public CalendarUsefulHabitsDto getInfoAboutUserHabits(String email) {
        List<Habit> allHabitsByUserId = findAllHabitsByStatus(email, true);

        Map<String, Integer> statisticByHabitsPerMonth =
            getAmountOfUnTakenItemsPerMonth(allHabitsByUserId);

        Map<String, Integer> statisticUnTakenItemsWithPrevMonth =
            getDifferenceItemsWithPrevMonth(allHabitsByUserId);

        CalendarUsefulHabitsDto dto = new CalendarUsefulHabitsDto();
        dto.setCreationDate(allHabitsByUserId.get(0).getCreateDate());
        dto.setAllItemsPerMonth(statisticByHabitsPerMonth);
        dto.setDifferenceUnTakenItemsWithPreviousMonth(statisticUnTakenItemsWithPrevMonth);

        return dto;
    }

    private Integer getItemsForPreviousMonth(Long habitId) {
        return habitStatisticRepo.getAmountOfItemsInPreviousMonth(habitId).orElse(0);
    }

    private Integer getItemsTakenToday(Long habitId) {
        return habitStatisticRepo.getAmountOfItemsToday(habitId).orElse(0);
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
                    habit -> getItemsTakenToday(habit.getId()) - getItemsForPreviousMonth(habit.getId())
                ));
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkovskyi
     */
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
                result.add(HabitStatisticDto.builder().createdOn(localDate).build());
            }
            localDate = localDate.plusDays(1);
        }
        return new HabitDto(habit.getId(), habit.getHabitDictionary().getName(), result);
    }
}