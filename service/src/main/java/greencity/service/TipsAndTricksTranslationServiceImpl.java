package greencity.service;

import greencity.dto.tipsandtricks.TextTranslationVO;
import greencity.dto.tipsandtricks.TitleTranslationVO;
import greencity.entity.TextTranslation;
import greencity.entity.TitleTranslation;
import greencity.repository.TextTranslationRepo;
import greencity.repository.TitleTranslationRepo;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TipsAndTricksTranslationServiceImpl implements TipsAndTricksTranslationService {
    private final TitleTranslationRepo titleTranslationRepo;
    private final TextTranslationRepo textTranslationRepo;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TitleTranslationVO> saveTitleTranslations(List<TitleTranslationVO> titleTranslations) {
        List<TitleTranslation> entityTitleTranslations =
            modelMapper.map(titleTranslations, new TypeToken<List<TitleTranslation>>() {
            }.getType());
        return modelMapper.map(titleTranslationRepo.saveAll(entityTitleTranslations),
            new TypeToken<List<TitleTranslationVO>>() {
            }.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TextTranslationVO> saveTextTranslations(List<TextTranslationVO> textTranslations) {
        List<TextTranslation> entityTextTranslations =
            modelMapper.map(textTranslations, new TypeToken<List<TextTranslation>>() {
            }.getType());
        return modelMapper.map(textTranslationRepo.saveAll(entityTextTranslations),
            new TypeToken<List<TextTranslationVO>>() {
            }.getType());
    }
}
