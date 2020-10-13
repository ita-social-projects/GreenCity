package greencity.service.impl;

import greencity.entity.TextTranslation;
import greencity.entity.TitleTranslation;
import greencity.repository.TextTranslationRepo;
import greencity.repository.TitleTranslationRepo;
import greencity.service.TipsAndTricksTranslationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class TipsAndTricksTranslationServiceImpl implements TipsAndTricksTranslationService {
    private final TitleTranslationRepo titleTranslationRepo;
    private final TextTranslationRepo textTranslationRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TitleTranslation> saveTitleTranslations(List<TitleTranslation> titleTranslations) {
        return titleTranslationRepo.saveAll(titleTranslations);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TextTranslation> saveTextTranslations(List<TextTranslation> textTranslations) {
        return textTranslationRepo.saveAll(textTranslations);
    }
}
