package greencity.service;

import greencity.entity.HabitDictionary;

public interface HabitDictionaryService {
    /**
     * Get {@link HabitDictionary} by id.
     *
     * @param id - id of {@link HabitDictionary}.
     * @return {@link HabitDictionary}.
     */
    HabitDictionary findById(Long id);
}
