package greencity.service;

import greencity.dto.advice.AdviceTranslationGeneralDto;
import greencity.dto.advice.AdvicePostDto;
import java.util.List;

/**
 * AdviceTranslationService interface.
 *
 * @author Vitaliy Dzen
 */
public interface AdviceTranslationService {
    /**
     * Method saves new {@link AdviceTranslationGeneralDto}.
     *
     * @param adviceTranslations {@link AdviceTranslationGeneralDto}
     * @return instance of {@link AdviceTranslationGeneralDto}
     * @author Vitaliy Dzen
     */
    List<AdviceTranslationGeneralDto> saveAdviceTranslation(List<AdviceTranslationGeneralDto> adviceTranslations);

    /**
     * Method saves new {@link AdviceTranslationGeneralDto} and
     * list of new {@link AdviceTranslationGeneralDto} with relationship to {@link AdviceTranslationGeneralDto}.
     *
     * @param advicePostDTO {@link AdvicePostDto}
     * @return List of {@link AdviceTranslationGeneralDto}
     * @author Vitaliy Dzen
     */
    List<AdviceTranslationGeneralDto> saveAdviceAndAdviceTranslation(AdvicePostDto advicePostDTO);
}
