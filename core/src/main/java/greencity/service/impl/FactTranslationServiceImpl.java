package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.advice.AdvicePostDTO;
import greencity.dto.fact.HabitFactPostDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.FactTranslation;
import greencity.entity.HabitFact;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.FactTranslationRepo;
import greencity.service.FactTranslationService;
import greencity.service.HabitFactService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import static greencity.constant.CacheConstants.HABIT_FACT_OF_DAY_CACHE;
import static greencity.entity.enums.FactOfDayStatus.CURRENT;

/**
 * Implementation of {@link FactTranslationService}.
 *
 * @author Vitaliy Dzen
 */
@EnableCaching
@Service
public class FactTranslationServiceImpl implements FactTranslationService {
    private final FactTranslationRepo factTranslationRepo;
    private final HabitFactService habitFactService;
    private final ModelMapper modelMapper;

    /**
     * Constructor with parameters.
     *
     * @author Vitaliy Dzen
     */
    @Autowired
    public FactTranslationServiceImpl(FactTranslationRepo factTranslationRepo, HabitFactService habitFactService,
                                      ModelMapper modelMapper) {
        this.factTranslationRepo = factTranslationRepo;
        this.habitFactService = habitFactService;
        this.modelMapper = modelMapper;
    }

    /**
     * Method saves new {@link HabitFact} and list of new {@link FactTranslation} with relationship
     * to {@link HabitFact}.
     *
     * @param habitFactPostDTO {@link AdvicePostDTO}
     * @return List of {@link FactTranslation}
     * @author Vitaliy Dzen
     */
    @Override
    public List<FactTranslation> saveHabitFactAndFactTranslation(HabitFactPostDTO habitFactPostDTO) {
        HabitFact habitFact = habitFactService.save(habitFactPostDTO);
        List<FactTranslation> factTranslations = modelMapper.map(habitFactPostDTO.getTranslations(),
            new TypeToken<List<FactTranslation>>() {
            }.getType());
        factTranslations.forEach(a -> a.setHabitFact(habitFact));

        return saveFactTranslation(factTranslations);
    }

    /**
     * Method saves new {@link FactTranslation}.
     *
     * @param factTranslations {@link FactTranslation}
     * @return List of {@link FactTranslation}
     * @author Vitaliy Dzen
     */
    @Override
    public List<FactTranslation> saveFactTranslation(List<FactTranslation> factTranslations) {
        return factTranslationRepo.saveAll(factTranslations);
    }

    /**
     * Method to get today's fact of day by language id.
     *
     * @param languageId id of language of the fact.
     * @return {@link LanguageTranslationDTO} of today's fact of day.
     */
    @Cacheable(value = HABIT_FACT_OF_DAY_CACHE)
    @Override
    public LanguageTranslationDTO getFactOfTheDay(Long languageId) {
        return modelMapper.map(
            factTranslationRepo.findAllByFactOfDayStatusAndLanguageId(CURRENT, languageId).orElseThrow(()
                -> new NotFoundException(ErrorMessage.FACT_OF_DAY_NOT_FOUND_BY_LANGUAGE_ID + languageId)).get(0),
            LanguageTranslationDTO.class);
    }
}

