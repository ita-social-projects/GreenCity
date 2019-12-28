package greencity.service;

import greencity.entity.FactTranslation;
import java.util.List;

/**
 * FactTranslationService interface.
 *
 * @author Vitaliy Dzen
 */
public interface FactTranslationService {
    /**
     * Method saves new {@link FactTranslation}.
     *
     * @param factTranslations {@link FactTranslation}
     * @return instance of {@link FactTranslation}
     * @author Vitaliy Dzen
     */
    List<FactTranslation> saveFactTranslation(List<FactTranslation> factTranslations);
}
