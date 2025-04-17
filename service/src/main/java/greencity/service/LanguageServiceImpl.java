package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageVO;
import greencity.entity.Language;
import greencity.exception.exceptions.LanguageNotFoundException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.LanguageRepo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link LanguageService}.
 */
@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {
    private final LanguageRepo languageRepo;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
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
    public LanguageDTO findByCode(String code) {
        return languageRepo.findByCode(code)
            .map(l -> modelMapper.map(l, LanguageDTO.class))
            .orElseThrow(() -> new LanguageNotFoundException(ErrorMessage.INVALID_LANGUAGE_CODE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> findAllLanguageCodes() {
        return languageRepo.findAllLanguageCodes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LanguageVO findById(Long id) {
        Language language = languageRepo.findById(id)
                .orElseThrow(() -> new NotFoundException());
        return modelMapper.map(language, LanguageVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean existsById(Long id) {
        return languageRepo.existsById(id);
    }
}
