package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.fact.HabitFactDTO;
import greencity.dto.fact.HabitFactPostDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.FactTranslation;
import greencity.entity.HabitFact;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.FactTranslationRepo;
import greencity.repository.HabitDictionaryRepo;
import greencity.repository.HabitFactRepo;
import greencity.service.HabitFactService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link HabitFactService}.
 *
 * @author Vitaliy Dzen
 */
@Service
public class HabitFactServiceImpl implements HabitFactService {
    private HabitFactRepo habitFactRepo;
    private HabitDictionaryRepo habitDictionaryRepo;
    private FactTranslationRepo factTranslationRepo;
    private final ModelMapper modelMapper;

    /**
     * Constructor with parameters.
     *
     * @author Vitaliy Dzen
     */
    @Autowired
    public HabitFactServiceImpl(ModelMapper modelMapper, HabitFactRepo habitFactRepo,
                                HabitDictionaryRepo habitDictionaryRepo, FactTranslationRepo factTranslationRepo) {
        this.modelMapper = modelMapper;
        this.habitFactRepo = habitFactRepo;
        this.habitDictionaryRepo = habitDictionaryRepo;
        this.factTranslationRepo = factTranslationRepo;
    }

    /**
     * Method finds all {@link HabitFact}.
     *
     * @return List of all {@link HabitFact}
     * @author Vitaliy Dzen
     */
    @Override
    public List<LanguageTranslationDTO> getAllHabitFacts() {
        List<FactTranslation> factTranslation = factTranslationRepo.findAll();
        return modelMapper.map(factTranslation, new TypeToken<List<LanguageTranslationDTO>>() {
        }.getType());
    }

    /**
     * Method finds random {@link HabitFact}.
     *
     * @return random {@link HabitFact}
     * @author Vitaliy Dzen
     */
    @Override
    public LanguageTranslationDTO getRandomHabitFactByHabitIdAndLanguage(Long id, String language) {
        return modelMapper.map(factTranslationRepo.getRandomFactTranslationByHabitIdAndLanguage(language, id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_FACT_NOT_FOUND_BY_ID + id)),
            LanguageTranslationDTO.class);
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
    public HabitFactDTO getHabitFactByName(String language, String name) {
        return modelMapper.map(factTranslationRepo.findFactTranslationByLanguage_CodeAndHabitFact(language, name)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_FACT_NOT_FOUND_BY_ID + name)),
            HabitFactDTO.class);
    }

    /**
     * Method saves new {@link HabitFact}.
     *
     * @param fact {@link HabitFactPostDTO}
     * @return instance of {@link HabitFact}
     * @author Vitaliy Dzen
     */
    @Override
    public HabitFact save(HabitFactPostDTO fact) {
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
