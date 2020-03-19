package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econews.EcoNewsDto;
import greencity.entity.EcoNews;
import greencity.entity.Tag;
import greencity.entity.localization.EcoNewsTranslation;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EcoNewsDtoMapperTest {
    @InjectMocks
    EcoNewsDtoMapper ecoNewsDtoMapper;

    @Test
    public void convertTest() {
        EcoNews ecoNews = ModelUtils.getEcoNews();
        EcoNewsTranslation ecoNewsTranslation = ModelUtils.getEcoNewsTranslation();
        ecoNewsTranslation.setEcoNews(ecoNews);

        EcoNewsDto expected = new EcoNewsDto(ecoNews.getCreationDate(), ecoNews.getImagePath(), 1L,
            ecoNewsTranslation.getTitle(), ecoNewsTranslation.getText(),
            ModelUtils.getEcoNewsAuthorDto(),
            ecoNews.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toList())
        );

        assertEquals(expected, ecoNewsDtoMapper.convert(ecoNewsTranslation));
    }
}
