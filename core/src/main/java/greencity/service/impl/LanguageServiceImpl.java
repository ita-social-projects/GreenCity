package greencity.service.impl;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.language.LanguageDTO;
import greencity.entity.Language;
import greencity.exception.exceptions.LanguageNotFoundException;
import greencity.repository.LanguageRepo;
import greencity.service.LanguageService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
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
    private HttpServletRequest request;

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
    public Language findByCode(String code) {
        return languageRepo.findByCode(code)
            .orElseThrow(() -> new LanguageNotFoundException(ErrorMessage.INVALID_LANGUAGE_CODE));
    }
}
