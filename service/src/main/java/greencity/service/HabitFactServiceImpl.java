package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.habit.HabitVO;
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
import greencity.exception.exceptions.WrongIdException;
import greencity.repository.HabitFactRepo;
import greencity.repository.HabitFactTranslationRepo;
import greencity.repository.HabitRepo;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link HabitFactService}.
 *
 * @author Vitaliy Dzen
 */
@Service
@AllArgsConstructor
public class HabitFactServiceImpl implements HabitFactService {
    private final HabitFactRepo habitFactRepo;
    private final HabitRepo habitRepo;
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
                Habit habit = habitRepo.findById(factDto.getHabit().getId())
                    .orElseThrow(() -> new WrongIdException(ErrorMessage.HABIT_NOT_FOUND_BY_ID));
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
        if (habitFactRepo.findById(id).isEmpty()) {
            throw new NotDeletedException(ErrorMessage.HABIT_FACT_NOT_DELETED_BY_ID);
        }
        habitFactRepo.deleteById(id);
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteAllByHabit(HabitVO habit) {
        habitFactRepo.findAllByHabitId(habit.getId())
            .forEach(habitFact -> {
                habitFactTranslationRepo.deleteAllByHabitFact(habitFact);
                habitFactRepo.delete(habitFact);
            });
    }
}
