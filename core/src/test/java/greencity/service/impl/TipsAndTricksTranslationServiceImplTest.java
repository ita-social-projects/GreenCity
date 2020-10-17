package greencity.service.impl;

import greencity.ModelUtils;
import greencity.entity.TextTranslation;
import greencity.entity.TitleTranslation;
import greencity.repository.TextTranslationRepo;
import greencity.repository.TitleTranslationRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void saveTitleTranslationsTest() {
        TitleTranslation titleTranslation = TitleTranslation.builder()
                .id(1L)
                .content("Content")
                .language(ModelUtils.getLanguage())
                .tipsAndTricks(ModelUtils.getTipsAndTricks())
                .build();
        List<TitleTranslation> titleTranslationList = Collections.singletonList(titleTranslation);

        when(titleTranslationRepo.saveAll(titleTranslationList)).thenReturn(titleTranslationList);
        assertEquals(titleTranslationList, tipsAndTricksTranslationService.saveTitleTranslations(titleTranslationList));
        verify(titleTranslationRepo).saveAll(titleTranslationList);
    }
    @Test
    void saveTextTranslationsTest() {
        TextTranslation textTranslation = TextTranslation.builder()
                .id(1L)
                .content("Content")
                .language(ModelUtils.getLanguage())
                .tipsAndTricks(ModelUtils.getTipsAndTricks())
                .build();
        List<TextTranslation> textTranslationList = Collections.singletonList(textTranslation);

        when(textTranslationRepo.saveAll(textTranslationList)).thenReturn(textTranslationList);
        assertEquals(textTranslationList, tipsAndTricksTranslationService.saveTextTranslations(textTranslationList));
        verify(textTranslationRepo).saveAll(textTranslationList);
    }

}