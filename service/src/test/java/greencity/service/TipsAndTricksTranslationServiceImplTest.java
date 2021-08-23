package greencity.service;

import greencity.ModelUtils;
import greencity.dto.tipsandtricks.TextTranslationVO;
import greencity.dto.tipsandtricks.TitleTranslationVO;
import greencity.entity.Language;
import greencity.entity.TextTranslation;
import greencity.entity.TitleTranslation;
import greencity.repository.TextTranslationRepo;
import greencity.repository.TitleTranslationRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class TipsAndTricksTranslationServiceImplTest {
    @InjectMocks
    private TipsAndTricksTranslationServiceImpl tipsAndTricksTranslationService;
    @Mock
    private TitleTranslationRepo titleTranslationRepo;
    @Mock
    private TextTranslationRepo textTranslationRepo;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTitleTranslationsTest() {
        TitleTranslationVO titleTranslationVO = ModelUtils.getTitleTranslationVO();
        List<TitleTranslationVO> titleTranslationVOList = Collections.singletonList(titleTranslationVO);

        TitleTranslation titleTranslation = TitleTranslation.builder()
            .id(titleTranslationVO.getId())
            .content(titleTranslationVO.getContent())
            .language(Language.builder()
                .id(titleTranslationVO.getLanguage().getId())
                .code(titleTranslationVO.getLanguage().getCode())
                .build())
            .build();
        List<TitleTranslation> titleTranslationList = Collections.singletonList(titleTranslation);

        when(modelMapper.map(titleTranslationVOList, new TypeToken<List<TitleTranslation>>() {
        }.getType())).thenReturn(titleTranslationList);

        when(titleTranslationRepo.saveAll(titleTranslationList)).thenReturn(titleTranslationList);

        when(modelMapper.map(titleTranslationList, new TypeToken<List<TitleTranslationVO>>() {
        }.getType())).thenReturn(titleTranslationVOList);

        titleTranslationRepo.saveAll(Collections.singletonList(titleTranslation));
        verify(titleTranslationRepo).saveAll(Collections.singletonList(titleTranslation));
    }

    @Test
    void saveTextTranslationsTest() {
        TextTranslationVO textTranslationVO = ModelUtils.getTextTranslationVO();
        List<TextTranslationVO> textTranslationVOList = Collections.singletonList(textTranslationVO);
        TextTranslation textTranslation = TextTranslation.builder()
            .id(textTranslationVO.getId())
            .content(textTranslationVO.getContent())
            .language(Language.builder()
                .id(textTranslationVO.getLanguage().getId())
                .code(textTranslationVO.getLanguage().getCode())
                .build())
            .build();
        List<TextTranslation> textTranslationList = Collections.singletonList(textTranslation);

        when(modelMapper.map(textTranslationVOList, new TypeToken<List<TextTranslation>>() {
        }.getType())).thenReturn(textTranslationList);
        when(textTranslationRepo.saveAll(textTranslationList)).thenReturn(textTranslationList);
        when(modelMapper.map(textTranslationList, new TypeToken<List<TextTranslationVO>>() {
        }.getType())).thenReturn(textTranslationVOList);

        textTranslationRepo.saveAll(Collections.singletonList(textTranslation));
        verify(textTranslationRepo).saveAll(Collections.singletonList(textTranslation));
    }
}
