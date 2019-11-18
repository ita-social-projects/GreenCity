package greencity.service;

import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import greencity.entity.Habit;
import greencity.entity.HabitStatistic;

public interface HabitStatisticService {
    /**
     * Method for creating new Habit statistic to database.
     *
     * @param addHabitStatisticDto - dto with {@link HabitStatistic} rate, amount of items, date and {@link Habit} id.
     * @return {@link AddHabitStatisticDto} instance.
     */
    AddHabitStatisticDto save(AddHabitStatisticDto addHabitStatisticDto);

    /**
     * Method for updating Habit statistic in database.
     *
     * @param dto - dto with {@link HabitStatistic} id, rate and amount of items.
     * @return {@link AddHabitStatisticDto} instance.
     */
    UpdateHabitStatisticDto update(Long habitStatisticId, UpdateHabitStatisticDto dto);

    /**
     * Method for finding a Habit Statistic by its id.
     *
     * @param id Habit Statistic's id
     * @return a Habit Statistic's instance
     */
    HabitStatistic findById(Long id);
}
