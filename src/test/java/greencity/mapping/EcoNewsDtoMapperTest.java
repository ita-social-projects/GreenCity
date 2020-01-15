package greencity.mapping;

import static org.junit.Assert.assertEquals;

import greencity.dto.econews.EcoNewsDto;
import greencity.entity.EcoNews;
import greencity.entity.Language;
import greencity.entity.localization.EcoNewsTranslation;
import java.time.ZonedDateTime;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EcoNewsDtoMapperTest {
    private EcoNewsDtoMapper ecoNewsDtoMapper = new EcoNewsDtoMapper();
    private Language language = new Language(1L, "en", Collections.emptyList(), Collections.emptyList(),
        Collections.emptyList());
    private EcoNews ecoNews = new EcoNews(1L, ZonedDateTime.now(), "text", "imagePath",
        Collections.emptyList());
    private EcoNewsTranslation ecoNewsTranslation = new EcoNewsTranslation(1L, language, "title", ecoNews);


    @Test
    public void convertTest() {
        EcoNewsDto expected = new EcoNewsDto(1L, ecoNewsTranslation.getTitle(), ecoNews.getCreationDate(),
            ecoNews.getText(), ecoNews.getImagePath());

        assertEquals(expected, ecoNewsDtoMapper.convert(ecoNewsTranslation));
    }
}
