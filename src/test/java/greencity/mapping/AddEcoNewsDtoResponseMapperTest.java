package greencity.mapping;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.entity.EcoNews;
import greencity.entity.Language;
import greencity.entity.localization.EcoNewsTranslation;
import greencity.repository.EcoNewsTranslationRepo;
import greencity.service.LanguageService;
import java.time.ZonedDateTime;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AddEcoNewsDtoResponseMapperTest {
    @Mock
    private LanguageService languageService;
    @Mock
    private EcoNewsTranslationRepo ecoNewsTranslationRepo;
    @InjectMocks
    private AddEcoNewsDtoResponseMapper mapper;

    private Language language = new Language(1L, "en", Collections.emptyList(), Collections.emptyList(),
        Collections.emptyList());

    private EcoNewsTranslation ecoNewsTranslation = new EcoNewsTranslation(1L, language, "title", null);

    private EcoNews ecoNews = new EcoNews(null, ZonedDateTime.now(), "text", "imagePath",
        Collections.singletonList(ecoNewsTranslation));

    @Test
    public void convertTest() {
        when(languageService.extractLanguageCodeFromRequest()).thenReturn("en");
        when(ecoNewsTranslationRepo.findByEcoNewsAndLanguageCode(ecoNews,
            languageService.extractLanguageCodeFromRequest())).thenReturn(ecoNewsTranslation);

        AddEcoNewsDtoResponse expected = new AddEcoNewsDtoResponse(ecoNews.getId(), ecoNewsTranslation.getTitle(),
            ecoNews.getText(), ecoNews.getCreationDate(), ecoNews.getImagePath());

        assertEquals(expected, mapper.convert(ecoNews));
    }
}
