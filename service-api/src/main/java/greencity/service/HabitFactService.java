package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.habitfact.HabitFactDto;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.language.LanguageTranslationDTO;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * HabitFactService interface.
 *
 * @author Vitaliy Dzen
 */
public interface HabitFactService {
    /**
     * Method finds all {HabitFact}.
     *
     * @return PageableDto of all {@link LanguageTranslationDTO}
     * @author Vitaliy Dzen
     */
    PageableDto<LanguageTranslationDTO> getAllHabitFacts(Pageable page, String language);

    /**
     * Method finds all {@code HabitFact} with all {@code HabitFactTranslation}'s.
     *
     * @param pageable - instance of {@link Pageable}.
     * @return list of {@link HabitFactVO}.
     * @author Ivan Behar
     */
    PageableDto<HabitFactVO> getAllHabitFactsVO(Pageable pageable);

    /**
     * Method finds random {HabitFact}.
     *
     * @return random {@link LanguageTranslationDTO}
     * @author Vitaliy Dzen
     */
    LanguageTranslationDTO getRandomHabitFactByHabitIdAndLanguage(Long id, String language);

    /**
     * Method find {HabitFact} by id.
     *
     * @param id of {HabitFact}
     * @return {@link HabitFactDto}
     * @author Vitaliy Dzen
     */
    HabitFactDto getHabitFactById(Long id);

    /**
     * Method find {HabitFact} by habitfact.
     *
     * @param name of {HabitFact}
     * @return {@link HabitFactDto}
     * @author Vitaliy Dzen
     */
    HabitFactDto getHabitFactByName(String language, String name);

    /**
     * Method saves new {HabitFact}.
     *
     * @param fact {@link HabitFactPostDto}
     * @return instance of {HabitFact}
     * @author Vitaliy Dzen
     */
    HabitFactVO save(HabitFactPostDto fact);

    /**
     * Method updates {HabitFact}.
     *
     * @param fact {@link HabitFactPostDto}
     * @return instance of {HabitFactVO}
     * @author Vitaliy Dzen
     */
    HabitFactVO update(HabitFactPostDto fact, Long id);

    /**
     * Method delete {HabitFact} by id.
     *
     * @param id Long
     * @return id of deleted {HabitFact}
     * @author Vitaliy Dzen
     */
    Long delete(Long id);

    /**
     * Method deletes all HabitFact
     * and HabitFactTranslation by list of IDs.
     *
     * @param listId list of id
     * @return listId list of id
     * @author Ivan Behar
     */
    List<Long> deleteAllHabitFactsByListOfId(List<Long> listId);

    /**
     * Method deletes all {@code HabitFact}'s by {@code Habit} instance.
     *
     * @param habit {@link HabitVO} instance.
     */
    void deleteAllByHabit(HabitVO habit);
}
