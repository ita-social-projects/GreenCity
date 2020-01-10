package greencity.service;

import greencity.dto.advice.AdvicePostDTO;
import greencity.entity.Advice;
import greencity.entity.AdviceTranslation;
import java.util.List;

/**
 * AdviceTranslationService interface.
 *
 * @author Vitaliy Dzen
 */
public interface AdviceTranslationService {
    /**
     * Method saves new {@link AdviceTranslation}.
     *
     * @param adviceTranslations {@link AdviceTranslation}
     * @return instance of {@link AdviceTranslation}
     * @author Vitaliy Dzen
     */
    List<AdviceTranslation> saveAdviceTranslation(List<AdviceTranslation> adviceTranslations);

    /**
     * Method saves new {@link Advice} and list of new {@link AdviceTranslation} with relationship to {@link Advice}.
     *
     * @param advicePostDTO {@link AdvicePostDTO}
     * @return List of {@link AdviceTranslation}
     * @author Vitaliy Dzen
     */
    List<AdviceTranslation> saveAdviceAndAdviceTranslation(AdvicePostDTO advicePostDTO);
}
