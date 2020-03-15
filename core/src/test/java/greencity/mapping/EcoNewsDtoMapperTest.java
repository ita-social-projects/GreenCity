package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.tag.TagDto;
import greencity.entity.EcoNews;
import greencity.entity.localization.EcoNewsTranslation;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

@RunWith(MockitoJUnitRunner.class)
public class EcoNewsDtoMapperTest {
    @Mock
    ModelMapper modelMapper;

    @Mock
    TagDtoMapper tagDtoMapper;

    @InjectMocks
    EcoNewsDtoMapper ecoNewsDtoMapper;

    @Test
    public void convertTest() {
        when(modelMapper.map(ModelUtils.getTag(), TagDto.class))
            .thenReturn(ModelUtils.getTagDto());
        when(tagDtoMapper.convert(ModelUtils.getTag()))
            .thenReturn(ModelUtils.getTagDto());

        EcoNews ecoNews = ModelUtils.getEcoNews();
        EcoNewsTranslation ecoNewsTranslation = ModelUtils.getEcoNewsTranslation();
        ecoNewsTranslation.setEcoNews(ecoNews);

        EcoNewsDto expected = new EcoNewsDto(ecoNews.getCreationDate(), ecoNews.getImagePath(), 1L,
            ecoNewsTranslation.getTitle(), ecoNewsTranslation.getText(),
            ModelUtils.getEcoNewsAuthorDto(),
            ecoNews.getTags().stream()
                .map(tag -> modelMapper.map(tag, TagDto.class))
                .collect(Collectors.toList())
        );

        assertEquals(expected, ecoNewsDtoMapper.convert(ecoNewsTranslation));
    }
}
