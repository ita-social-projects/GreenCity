package greencity.service;

import greencity.dto.advice.AdvicePostDTO;
import greencity.dto.fact.HabitFactPostDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.FactTranslation;
import greencity.entity.HabitFact;
import java.util.List;

/**
 * FactTranslationService interface.
 *
 * @author Vitaliy Dzen
 */
public interface FactTranslationService {
    /**
     * Method saves new {@link FactTranslation}.
     *
     * @param factTranslations {@link FactTranslation}
     * @return instance of {@link FactTranslation}
     * @author Vitaliy Dzen
     */
    List<FactTranslation> saveFactTranslation(List<FactTranslation> factTranslations);

    /**
     * Method saves new {@link HabitFact} and list of new {@link FactTranslation} with relationship
     * to {@link HabitFact}.
     *
     * @param habitFactPostDTO {@link AdvicePostDTO}
     * @return List of {@link FactTranslation}
     * @author Vitaliy Dzen
     */
    List<FactTranslation> saveHabitFactAndFactTranslation(HabitFactPostDTO habitFactPostDTO);

    /**
     * Method to get today's fact of day by language id.
     *
     * @param languageId id of language of the fact.
     * @return {@link LanguageTranslationDTO} of today's fact of day.
     */
    LanguageTranslationDTO getFactOfTheDay(Long languageId);
}
