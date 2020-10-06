package greencity.service;

import greencity.dto.advice.AdvicePostDTO;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.HabitFactTranslation;
import greencity.entity.HabitFact;
import greencity.entity.Language;
import java.util.List;

/**
 * FactTranslationService interface.
 *
 * @author Vitaliy Dzen
 */
public interface HabitFactTranslationService {
    /**
     * Method saves new {@link HabitFactTranslation}.
     *
     * @param habitFactTranslations {@link HabitFactTranslation}.
     * @return instance of {@link HabitFactTranslation}.
     * @author Vitaliy Dzen.
     */
    List<HabitFactTranslation> saveHabitFactTranslation(List<HabitFactTranslation> habitFactTranslations);

    /**
     * Method saves new {@link HabitFact} and list of new {@link HabitFactTranslation} with relationship
     * to {@link HabitFact}.
     *
     * @param habitFactPostDTO {@link AdvicePostDTO}.
     * @return List of {@link HabitFactTranslation}.
     * @author Vitaliy Dzen.
     */
    List<HabitFactTranslation> saveHabitFactAndFactTranslation(HabitFactPostDto habitFactPostDTO);

    /**
     * Method to get today's {@link HabitFact} of day by language id.
     *
     * @param languageId id of {@link Language}.
     * @return {@link LanguageTranslationDTO} of today's {@link HabitFact} of day.
     */
    LanguageTranslationDTO getHabitFactOfTheDay(Long languageId);
}
