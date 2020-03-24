package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.entity.EcoNews;
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
    private EcoNewsAuthorDtoMapper ecoNewsAuthorDtoMapper;
    @InjectMocks
    private AddEcoNewsDtoResponseMapper mapper;

    @Test
    public void convertTest() {
        EcoNews ecoNews = ModelUtils.getEcoNews();
        when(ecoNewsAuthorDtoMapper.convert(ecoNews.getAuthor()))
            .thenReturn(ModelUtils.getEcoNewsAuthorDto());

        AddEcoNewsDtoResponse expected = ModelUtils.getAddEcoNewsDtoResponse();
        expected.setCreationDate(ecoNews.getCreationDate());
        expected.setTitle(null);
        expected.setText(null);

        assertEquals(expected, mapper.convert(ecoNews));
    }
}
