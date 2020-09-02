package greencity.service;

import greencity.dto.fact.HabitFactDTO;
import greencity.dto.fact.HabitFactPostDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.HabitFact;
import java.util.List;

/**
 * HabitFactService interface.
 *
 * @author Vitaliy Dzen
 */
public interface HabitFactService {
    /**
     * Method finds all {@link HabitFact}.
     *
     * @return List of all {@link HabitFact}
     * @author Vitaliy Dzen
     */
    List<LanguageTranslationDTO> getAllHabitFacts();

    /**
     * Method finds random {@link HabitFact}.
     *
     * @return random {@link HabitFact}
     * @author Vitaliy Dzen
     */
    LanguageTranslationDTO getRandomHabitFactByHabitIdAndLanguage(Long id, String language);

    /**
     * Method find {@link HabitFact} by id.
     *
     * @param id of {@link HabitFact}
     * @return {@link HabitFactDTO}
     * @author Vitaliy Dzen
     */
    HabitFactDTO getHabitFactById(Long id);

    /**
     * Method find {@link HabitFact} by fact.
     *
     * @param name of {@link HabitFact}
     * @return {@link HabitFactDTO}
     * @author Vitaliy Dzen
     */
    HabitFactDTO getHabitFactByName(String language, String name);

    /**
     * Method saves new {@link HabitFact}.
     *
     * @param fact {@link HabitFactPostDTO}
     * @return instance of {@link HabitFact}
     * @author Vitaliy Dzen
     */
    HabitFact save(HabitFactPostDTO fact);

    /**
     * Method updates {@link HabitFact}.
     *
     * @param fact {@link HabitFactPostDTO}
     * @return instance of {@link HabitFact}
     * @author Vitaliy Dzen
     */
    HabitFact update(HabitFactPostDTO fact, Long id);

    /**
     * Method delete {@link HabitFact} by id.
     *
     * @param id Long
     * @return id of deleted {@link HabitFact}
     * @author Vitaliy Dzen
     */
    Long delete(Long id);
}
