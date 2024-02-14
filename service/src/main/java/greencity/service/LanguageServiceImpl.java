package greencity.service;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.language.LanguageDTO;
import greencity.entity.Language;
import greencity.exception.exceptions.LanguageNotFoundException;
import greencity.repository.LanguageRepo;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link LanguageService}.
 *
 * @author Oleh Kopylchak
 * @author Vitaliy Dzen
 */
@Service
public class LanguageServiceImpl implements LanguageService {
    private final LanguageRepo languageRepo;
    private final ModelMapper modelMapper;
    private final HttpServletRequest request;

    /**
     * Constructor with parameters.
     *
     * @author Vitaliy Dzen
     */
    @Autowired
    public LanguageServiceImpl(
        LanguageRepo languageRepo,
        @Lazy ModelMapper modelMapper, HttpServletRequest request) {
        this.languageRepo = languageRepo;
        this.modelMapper = modelMapper;
        this.request = request;
    }

    /**
     * Method finds all {@link Language}.
     *
     * @return List of all {@link LanguageDTO}
     * @author Vitaliy Dzen
     */
    @Override
    public List<LanguageDTO> getAllLanguages() {
        return modelMapper.map(languageRepo.findAll(), new TypeToken<List<LanguageDTO>>() {
        }.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String extractLanguageCodeFromRequest() {
        String languageCode = request.getParameter("language");

        if (languageCode == null) {
            return AppConstant.DEFAULT_LANGUAGE_CODE;
        }

        return languageCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LanguageDTO findByCode(String code) {
        Language language = languageRepo.findByCode(code)
            .orElseThrow(() -> new LanguageNotFoundException(ErrorMessage.INVALID_LANGUAGE_CODE));
        return modelMapper.map(language, LanguageDTO.class);
    }

    /**
     * method, that returns codes of all {@link Language}s.
     *
     * @return {@link List} of language code strings.
     */
    @Override
    public List<String> findAllLanguageCodes() {
        return languageRepo.findAllLanguageCodes();
    }

    /**
     * Method for getting {@link LanguageDTO} by tagTranslationId.
     *
     * @param tagTranslationId id of tag translation object.
     * @return {@link LanguageDTO}.
     *
     * @author Vira Maksymets
     */
    @Override
    public LanguageDTO findByTagTranslationId(Long tagTranslationId) {
        Language language = languageRepo.findByTagTranslationId(tagTranslationId).orElseThrow();
        return modelMapper.map(language, LanguageDTO.class);
    }
}
