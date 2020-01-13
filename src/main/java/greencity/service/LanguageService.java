package greencity.service;

import greencity.dto.language.LanguageDTO;
import greencity.entity.Language;
import java.util.List;

/**
 * LanguageService interface.
 *
 * @author Vitaliy Dzen
 */
public interface LanguageService {
    /**
     * Method finds all {@link Language}.
     *
     * @return List of all {@link LanguageDTO}
     * @author Vitaliy Dzen
     */
    List<LanguageDTO> getAllLanguages();
}
