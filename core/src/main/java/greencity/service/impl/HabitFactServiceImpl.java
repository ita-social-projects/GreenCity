package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.habitfact.HabitFactDto;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.Habit;
import greencity.entity.HabitFact;
import greencity.entity.HabitFactTranslation;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.HabitFactRepo;
import greencity.repository.HabitFactTranslationRepo;
import greencity.service.HabitFactService;
import greencity.service.HabitService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link HabitFactService}.
 *
 * @author Vitaliy Dzen
 */
@Service
@AllArgsConstructor
public class HabitFactServiceImpl implements HabitFactService {
    private final HabitFactRepo habitFactRepo;
    private final HabitService habitService;
    private final HabitFactTranslationRepo habitFactTranslationRepo;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LanguageTranslationDTO> getAllHabitFacts() {
        List<HabitFactTranslation> habitFactTranslation = habitFactTranslationRepo.findAll();
        return modelMapper.map(habitFactTranslation, new TypeToken<List<LanguageTranslationDTO>>() {
        }.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LanguageTranslationDTO getRandomHabitFactByHabitIdAndLanguage(Long id, String language) {
        return modelMapper.map(habitFactTranslationRepo.getRandomHabitFactTranslationByHabitIdAndLanguage(language, id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_FACT_NOT_FOUND_BY_ID + id)),
            LanguageTranslationDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitFactDto getHabitFactById(Long id) {
        return modelMapper.map(habitFactRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_FACT_NOT_FOUND_BY_ID + id)),
            HabitFactDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitFactDto getHabitFactByName(String language, String name) {
        return modelMapper.map(habitFactTranslationRepo.findFactTranslationByLanguage_CodeAndHabitFact(language, name)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_FACT_NOT_FOUND_BY_ID + name)),
            HabitFactDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitFact save(HabitFactPostDto fact) {
        return habitFactRepo.save(modelMapper.map(fact, HabitFact.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitFact update(HabitFactPostDto factDto, Long id) {
        return habitFactRepo.findById(id)
            .map(employee -> {
                Habit habit = habitService.getById(factDto.getHabit().getId());
                employee.setHabit(habit);
                return habitFactRepo.save(employee);
            })
            .orElseThrow(() -> new NotUpdatedException(ErrorMessage.HABIT_FACT_NOT_UPDATED_BY_ID));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long delete(Long id) {
        if (habitFactRepo.findById(id).isEmpty()) {
            throw new NotDeletedException(ErrorMessage.HABIT_FACT_NOT_DELETED_BY_ID);
        }
        habitFactRepo.deleteById(id);
        return id;
    }
}
