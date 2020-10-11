package greencity.service;

import greencity.dto.advice.AdviceDto;
import greencity.dto.advice.AdvicePostDto;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.Advice;
import java.util.List;

/**
 * AdviceService interface.
 *
 * @author Vitaliy Dzen
 */
public interface AdviceService {
    /**
     * Method finds all {@link Advice}.
     *
     * @return List of all {@link Advice}
     * @author Vitaliy Dzen
     */
    List<LanguageTranslationDTO> getAllAdvices();

    /**
     * Method finds random {@link Advice}.
     *
     * @return random {@link Advice}
     * @author Vitaliy Dzen
     */
    LanguageTranslationDTO getRandomAdviceByHabitIdAndLanguage(Long id, String language);

    /**
     * Method find {@link Advice} by id.
     *
     * @param id of {@link Advice}
     * @return {@link AdviceDto}
     * @author Vitaliy Dzen
     */
    AdviceDto getAdviceById(Long id);

    /**
     * Method find {@link Advice} by content.
     *
     * @param name of {@link Advice}
     * @return {@link AdviceDto}
     * @author Vitaliy Dzen
     */
    AdviceDto getAdviceByName(String language, String name);

    /**
     * Method saves new {@link Advice}.
     *
     * @param advice {@link AdviceDto}
     * @return instance of {@link Advice}
     * @author Vitaliy Dzen
     */
    Advice save(AdvicePostDto advice);

    /**
     * Method updates {@link Advice}.
     *
     * @param advice {@link AdviceDto}
     * @return instance of {@link Advice}
     * @author Vitaliy Dzen
     */
    Advice update(AdvicePostDto advice, Long id);

    /**
     * Method delete {@link Advice} by id.
     *
     * @param id Long
     * @return id of deleted {@link Advice}
     * @author Vitaliy Dzen
     */
    Long delete(Long id);
}
