package greencity.service;

import greencity.dto.habitfact.HabitFactDtoResponse;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactTranslationVO;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.language.LanguageTranslationDTO;
import java.util.List;

/**
 * FactTranslationService interface.
 *
 * @author Vitaliy Dzen
 */
public interface HabitFactTranslationService {
    /**
     * Method saves new {HabitFactTranslation}.
     *
     * @param habitFactTranslations {@link HabitFactTranslationVO}.
     * @return instance of {@link HabitFactTranslationVO}.
     * @author Vitaliy Dzen.
     */
    List<HabitFactTranslationVO> saveHabitFactTranslation(List<HabitFactTranslationVO> habitFactTranslations);

    /**
     * Method saves new {HabitFact} and list of new {@link HabitFactTranslationVO}
     * with relationship to {HabitFact}.
     *
     * @param habitFactPostDTO {@link greencity.dto.habitfact.HabitFactDto}.
     * @return {@link HabitFactVO}.
     * @author Vitaliy Dzen.
     */
    HabitFactDtoResponse saveHabitFactAndFactTranslation(HabitFactPostDto habitFactPostDTO);

    /**
     * Method to get today's {HabitFact} of day by language id.
     *
     * @param languageId id of {Language}.
     * @return {@link LanguageTranslationDTO} of today's {HabitFact} of day.
     */
    LanguageTranslationDTO getHabitFactOfTheDay(Long languageId);
}
