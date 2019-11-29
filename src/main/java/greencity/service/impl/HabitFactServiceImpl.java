package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.fact.HabitFactDTO;
import greencity.entity.HabitFact;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.HabitFactRepo;
import greencity.service.AdviceService;
import greencity.service.HabitFactService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link AdviceService}.
 *
 * @author Vitaliy Dzen
 */
@Service
@AllArgsConstructor
public class HabitFactServiceImpl implements HabitFactService {
    private HabitFactRepo adviceRepo;

    /**
     * Method finds random {@link HabitFact}.
     *
     * @return random {@link HabitFact}
     * @author Vitaliy Dzen
     */
    @Override
    public HabitFactDTO getRandomHabitFactByHabitId(Long id) {
        return new HabitFactDTO(adviceRepo.getRandomHabitFactByHabitId(id).orElseThrow(()
            -> new NotFoundException(ErrorMessage.HABIT_FACT_NOT_FOUND_BY_ID + id)));
    }
}
