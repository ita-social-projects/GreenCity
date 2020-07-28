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

    /**
     * Method for extracting language code from request param.
     *
     * @return language code
     */
    String extractLanguageCodeFromRequest();

    /**
     * Method for getting {@link Language} by code.
     *
     * @param code code of language.
     * @return {@link Language} by language code.
     */
    Language findByCode(String code);

    /**
     * method, that returns codes of all {@link Language}s.
     *
     * @return {@link List} of language code strings.
     */
    List<String> findAllLanguageCodes();
}
