package greencity.service;

import greencity.dto.fact.HabitFactDTO;
import greencity.entity.Advice;

/**
 * AdviceService interface.
 *
 * @author Vitaliy Dzen
 */
public interface HabitFactService {
    /**
     * Method finds random {@link Advice}.
     *
     * @return random {@link Advice}
     * @author Vitaliy Dzen
     */
    HabitFactDTO getRandomHabitFactByHabitId(Long id);
}
