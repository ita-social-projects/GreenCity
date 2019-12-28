package greencity.service;

import greencity.dto.advice.AdviceDTO;
import greencity.dto.advice.AdvicePostDTO;
import greencity.dto.advice.AdviceTranslationDTO;
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
    List getAllAdvices();

    /**
     * Method finds random {@link Advice}.
     *
     * @return random {@link Advice}
     * @author Vitaliy Dzen
     */
    AdviceTranslationDTO getRandomAdviceByHabitIdAndLanguage(Long id, String language);

    /**
     * Method find {@link Advice} by id.
     *
     * @param id of {@link Advice}
     * @return {@link AdviceDTO}
     * @author Vitaliy Dzen
     */
    AdviceDTO getAdviceById(Long id);

    /**
     * Method find {@link Advice} by content.
     *
     * @param name of {@link Advice}
     * @return {@link AdviceDTO}
     * @author Vitaliy Dzen
     */
    AdviceDTO getAdviceByName(String language, String name);

    /**
     * Method saves new {@link Advice}.
     *
     * @param advice {@link AdviceDTO}
     * @return instance of {@link Advice}
     * @author Vitaliy Dzen
     */
    Advice save(AdvicePostDTO advice);

    /**
     * Method updates {@link Advice}.
     *
     * @param advice {@link AdviceDTO}
     * @return instance of {@link Advice}
     * @author Vitaliy Dzen
     */
    Advice update(AdvicePostDTO advice, Long id);

    /**
     * Method delete {@link Advice} by id.
     *
     * @param id Long
     * @return id of deleted {@link Advice}
     * @author Vitaliy Dzen
     */
    Long delete(Long id);
}
