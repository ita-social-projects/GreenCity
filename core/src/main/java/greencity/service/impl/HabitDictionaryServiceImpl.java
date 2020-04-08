package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.entity.HabitDictionary;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.HabitDictionaryRepo;
import greencity.service.HabitDictionaryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HabitDictionaryServiceImpl implements HabitDictionaryService {
    private final HabitDictionaryRepo habitDictionaryRepo;

    /**
     * Get {@link HabitDictionary} by id.
     *
     * @param id - id of {@link HabitDictionary}.
     * @return {@link HabitDictionary}.
     * @author Kovaliv Taras.
     */
    @Override
    public HabitDictionary findById(Long id) {
        return habitDictionaryRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + id));
    }
}
