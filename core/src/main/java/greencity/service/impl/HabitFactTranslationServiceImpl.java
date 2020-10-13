package greencity.service.impl;

import static greencity.constant.CacheConstants.HABIT_FACT_OF_DAY_CACHE;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.HabitFact;
import greencity.entity.HabitFactTranslation;
import greencity.entity.Language;
import static greencity.entity.enums.FactOfDayStatus.CURRENT;
import greencity.repository.HabitFactTranslationRepo;
import greencity.service.HabitFactService;
import greencity.service.HabitFactTranslationService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link HabitFactTranslationService}.
 *
 * @author Vitaliy Dzen
 */
@EnableCaching
@Service
@AllArgsConstructor
public class HabitFactTranslationServiceImpl implements HabitFactTranslationService {
    private final HabitFactTranslationRepo habitFactTranslationRepo;
    private final HabitFactService habitFactService;
    private final ModelMapper modelMapper;

    /**
     * Method saves new {@link HabitFact} and list of new {@link HabitFactTranslation} with relationship
     * to {@link HabitFact}.
     *
     * @param habitFactPostDTO {@link HabitFactPostDto}.
     * @return List of {@link HabitFactTranslation}.
     * @author Vitaliy Dzen.
     */
    @Override
    public List<HabitFactTranslation> saveHabitFactAndFactTranslation(HabitFactPostDto habitFactPostDTO) {
        HabitFact habitFact = habitFactService.save(habitFactPostDTO);
        List<HabitFactTranslation> habitFactTranslations = modelMapper.map(habitFactPostDTO.getTranslations(),
            new TypeToken<List<HabitFactTranslation>>() {
            }.getType());
        habitFactTranslations.forEach(a -> a.setHabitFact(habitFact));

        return saveHabitFactTranslation(habitFactTranslations);
    }

    /**
     * Method saves new {@link HabitFactTranslation}.
     *
     * @param habitFactTranslations {@link HabitFactTranslation}.
     * @return List of {@link HabitFactTranslation}.
     * @author Vitaliy Dzen.
     */
    @Override
    public List<HabitFactTranslation> saveHabitFactTranslation(List<HabitFactTranslation> habitFactTranslations) {
        return habitFactTranslationRepo.saveAll(habitFactTranslations);
    }

    /**
     * Method to get today's {@link HabitFact} of day by {@link Language} id.
     *
     * @param languageId id of {@link Language} of the {@link HabitFact}.
     * @return {@link LanguageTranslationDTO} of today's {@link HabitFact} of day.
     */
    @Cacheable(value = HABIT_FACT_OF_DAY_CACHE)
    @Override
    public LanguageTranslationDTO getHabitFactOfTheDay(Long languageId) {
        return modelMapper.map(
            habitFactTranslationRepo.findAllByFactOfDayStatusAndLanguageId(CURRENT, languageId),
            LanguageTranslationDTO.class);
    }
}

