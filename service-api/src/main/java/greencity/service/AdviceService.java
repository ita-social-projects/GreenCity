package greencity.service;

import greencity.dto.advice.AdviceDto;
import greencity.dto.advice.AdviceVO;
import greencity.dto.advice.AdvicePostDto;
import greencity.dto.language.LanguageTranslationDTO;
import java.util.List;

/**
 * AdviceService interface.
 *
 * @author Vitaliy Dzen
 */
public interface AdviceService {
    /**
     * Method finds all {@link AdviceDto}.
     *
     * @return List of all {@link AdviceDto}
     * @author Vitaliy Dzen
     */
    List<LanguageTranslationDTO> getAllAdvices();

    /**
     * Method finds random {@link AdviceDto}.
     *
     * @return random {@link AdviceDto}
     * @author Vitaliy Dzen
     */
    LanguageTranslationDTO getRandomAdviceByHabitIdAndLanguage(Long id, String language);

    /**
     * Method find {@link AdviceDto} by id.
     *
     * @param id of {@link AdviceDto}
     * @return {@link AdviceDto}
     * @author Vitaliy Dzen
     */
    AdviceDto getAdviceById(Long id);

    /**
     * Method find {@link AdviceDto} by content.
     *
     * @param name of {@link AdviceDto}
     * @return {@link AdviceDto}
     * @author Vitaliy Dzen
     */
    AdviceDto getAdviceByName(String language, String name);

    /**
     * Method saves new {@link AdvicePostDto}.
     *
     * @param advice {@link AdvicePostDto}
     * @return instance of {@link AdviceVO}
     * @author Vitaliy Dzen
     */
    AdviceVO save(AdvicePostDto advice);

    /**
     * Method updates {@link AdviceVO}.
     *
     * @param advice {@link AdvicePostDto}
     * @return instance of {@link AdviceVO}
     * @author Vitaliy Dzen
     */
    AdviceVO update(AdvicePostDto advice, Long id);

    /**
     * Method delete {@link AdviceDto} by id.
     *
     * @param id Long
     * @return id of deleted {@link AdviceDto}
     * @author Vitaliy Dzen
     */
    Long delete(Long id);
}
