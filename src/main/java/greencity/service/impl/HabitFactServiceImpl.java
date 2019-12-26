package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.advice.AdviceDTO;
import greencity.dto.fact.HabitFactDTO;
import greencity.dto.fact.HabitFactPostDTO;
import greencity.entity.HabitFact;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.HabitDictionaryRepo;
import greencity.repository.HabitFactRepo;
import greencity.service.AdviceService;
import greencity.service.HabitFactService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link AdviceService}.
 *
 * @author Vitaliy Dzen
 */
@Service
@AllArgsConstructor
public class HabitFactServiceImpl implements HabitFactService {
    private HabitFactRepo habitFactRepo;
    private HabitDictionaryRepo habitDictionaryRepo;

    @Autowired
    private final ModelMapper modelMapper;

    /**
     * Method finds all {@link HabitFact}.
     *
     * @return List of all {@link HabitFact}
     * @author Vitaliy Dzen
     */
    @Override
    public List<HabitFactDTO> getAllHabitFacts() {
        return modelMapper.map(habitFactRepo.findAll(), new TypeToken<List<HabitFactDTO>>() {
        }.getType());
    }

    /**
     * Method finds random {@link HabitFact}.
     *
     * @return random {@link HabitFact}
     * @author Vitaliy Dzen
     */
    @Override
    public HabitFactDTO getRandomHabitFactByHabitId(Long id) {
        return modelMapper.map(habitFactRepo.getRandomHabitFactByHabitId(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_FACT_NOT_FOUND_BY_ID + id)),
            HabitFactDTO.class);
    }

    /**
     * Method find {@link HabitFact} by id.
     *
     * @param id of {@link HabitFact}
     * @return {@link HabitFactDTO}
     * @author Vitaliy Dzen
     */
    @Override
    public HabitFactDTO getHabitFactById(Long id) {
        return modelMapper.map(habitFactRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_FACT_NOT_FOUND_BY_ID + id)),
            HabitFactDTO.class);
    }

    /**
     * Method find {@link HabitFact} by fact.
     *
     * @param name of {@link HabitFact}
     * @return {@link HabitFactDTO}
     * @author Vitaliy Dzen
     */
    @Override
    public HabitFactDTO getHabitFactByName(String name) {
        return modelMapper.map(habitFactRepo.findHabitFactByFact(name)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_FACT_NOT_FOUND_BY_ID + name)),
            HabitFactDTO.class);
    }

    /**
     * Method saves new {@link HabitFact}.
     *
     * @param fact {@link AdviceDTO}
     * @return instance of {@link HabitFact}
     * @author Vitaliy Dzen
     */
    @Override
    public HabitFact save(HabitFactPostDTO fact) {
        if (habitFactRepo.findHabitFactByFact(fact.getFact()).isPresent()) {
            throw new NotSavedException(ErrorMessage.HABIT_FACT_NOT_SAVED_BY_NAME);
        }
        return habitFactRepo.save(modelMapper.map(fact, HabitFact.class));
    }

    /**
     * Method updates {@link HabitFact}.
     *
     * @param fact {@link HabitFactPostDTO} Object
     * @param id   of {@link HabitFact}
     * @return instance of {@link HabitFact}
     * @author Vitaliy Dzen
     */
    @Override
    public HabitFact update(HabitFactPostDTO fact, Long id) {
        return habitFactRepo.findById(id)
            .map(employee -> {
                employee.setHabitDictionary(habitDictionaryRepo.findById(fact.getHabitDictionary().getId()).get());
                employee.setFact(fact.getFact());
                return habitFactRepo.save(employee);
            })
            .orElseThrow(() -> new NotUpdatedException(ErrorMessage.HABIT_FACT_NOT_UPDATED_BY_ID));
    }

    @Override
    public Long delete(Long id) {
        if (!(habitFactRepo.findById(id).isPresent())) {
            throw new NotDeletedException(ErrorMessage.HABIT_FACT_NOT_DELETED_BY_ID);
        }
        habitFactRepo.deleteById(id);
        return id;
    }
}
