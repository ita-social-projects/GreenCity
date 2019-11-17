package greencity.service;

import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.habitstatistic.HabitStatisticForUpdateDto;
import greencity.entity.Habit;
import greencity.entity.HabitStatistic;

public interface HabitStatisticService {
    /**
     * Method for creating new Habit statistic to database.
     *
     * @param habitStatisticDto - dto with {@link HabitStatistic} rate, amount of items, date and {@link Habit} id.
     * @return {@link HabitStatisticDto} instance.
     */
    HabitStatisticDto save(HabitStatisticDto habitStatisticDto);

    /**
     * Method for updating Habit statistic in database.
     *
     * @param dto - dto with {@link HabitStatistic} id, rate and amount of items.
     * @return {@link HabitStatisticDto} instance.
     */
    HabitStatisticForUpdateDto update(HabitStatisticForUpdateDto dto);

    /**
     * Method for finding a Habit Statistic by its id.
     *
     * @param id Habit Statistic's id
     * @return a Habit Statistic's instance
     */
    HabitStatistic findById(Long id);
}
