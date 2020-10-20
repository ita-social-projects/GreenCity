package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.habitfact.HabitFactDto;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactVO;
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
import java.util.Optional;

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
    public HabitFactVO save(HabitFactPostDto fact) {
        HabitFact map = modelMapper.map(fact, HabitFact.class);
        return modelMapper.map(habitFactRepo.save(map), HabitFactVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitFactVO update(HabitFactPostDto factDto, Long id) {
        return Optional.of(modelMapper.map(habitFactRepo.findById(id)
            .map(employee -> {
                Habit habit = modelMapper.map(habitService.getById(factDto.getHabit().getId()), Habit.class);
                employee.setHabit(habit);
                return modelMapper.map(habitFactRepo.save(employee), HabitFactVO.class);
            }), HabitFactVO.class))
            .orElseThrow(() -> new NotUpdatedException(ErrorMessage.HABIT_FACT_NOT_UPDATED_BY_ID));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long delete(Long id) {
        if (!(habitFactRepo.findById(id).isPresent())) {
            throw new NotDeletedException(ErrorMessage.HABIT_FACT_NOT_DELETED_BY_ID);
        }
        habitFactRepo.deleteById(id);
        return id;
    }
}
