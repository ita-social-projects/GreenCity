package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.entity.EcoNews;
import greencity.repository.EcoNewsTranslationRepo;
import greencity.service.LanguageService;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AddEcoNewsDtoResponseMapperTest {
    @Mock
    private LanguageService languageService;
    @Mock
    private EcoNewsTranslationRepo ecoNewsTranslationRepo;
    @Mock
    private EcoNewsAuthorDtoMapper ecoNewsAuthorDtoMapper;
    @InjectMocks
    private AddEcoNewsDtoResponseMapper mapper;

    @Test
    public void convertTest() {
        EcoNews ecoNews = ModelUtils.getEcoNews();
        when(languageService.extractLanguageCodeFromRequest()).thenReturn("en");
        when(ecoNewsTranslationRepo.findByEcoNewsAndLanguageCode(ecoNews,
            languageService.extractLanguageCodeFromRequest()))
            .thenReturn(ModelUtils.getEcoNewsTranslation());
        when(ecoNewsAuthorDtoMapper.convert(ecoNews.getAuthor()))
            .thenReturn(ModelUtils.getEcoNewsAuthorDto());

        AddEcoNewsDtoResponse expected = ModelUtils.getAddEcoNewsDtoResponse();
        expected.setCreationDate(ecoNews.getCreationDate());

        assertEquals(expected, mapper.convert(ecoNews));
    }
}
