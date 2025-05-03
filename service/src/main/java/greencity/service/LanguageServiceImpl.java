package greencity.service;

import greencity.client.UserRemoteClient;
import greencity.dto.language.LanguageDTO;
import java.util.List;

import greencity.dto.language.LanguageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link LanguageService}.
 */
@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {
    private final UserRemoteClient userRemoteClient;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LanguageDTO> getAllLanguages() {
        return userRemoteClient.getAllLanguages();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LanguageDTO findByCode(String code) {
        return userRemoteClient.findLanguageByCode(code);
    }

    @Override
    public LanguageVO findByCodeTemp(String code) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LanguageVO findById(Long id) {
        return userRemoteClient.findByLanguageId(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> findAllLanguageCodes() {
        return userRemoteClient.findAllLanguageCodes();
    }
}
