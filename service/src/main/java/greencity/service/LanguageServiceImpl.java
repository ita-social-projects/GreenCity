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
    // TODO: move LanguageNotFoundException to user
    @Override
    public LanguageDTO findByCode(String code) {
        return userRemoteClient.findLanguageByCode(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LanguageVO findById(Long languageId) {
        return userRemoteClient.findLanguageById(languageId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> findAllLanguageCodes() {
        return userRemoteClient.findAllLanguageCodes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> findAllLanguageIds() {
        return userRemoteClient.findAllLanguageIds();
    }

    @Override
    public String findLanguageCodeById(Long languageId) {
        return "";
    }

    @Override
    public Long findLanguageIdByCode(String languageCode) {
        return 0L;
    }

    @Override
    public LanguageDTO findLanguageByCode(String code) {
        return null;
    }

    @Override
    public LanguageVO findLanguageById(Long languageId) {
        return null;
    }
}
