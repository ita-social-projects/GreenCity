package greencity.service;

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
}
