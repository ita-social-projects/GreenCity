package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.entity.EcoNews;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AddEcoNewsDtoRequestMapperTest {
    @InjectMocks
    private AddEcoNewsDtoRequestMapper mapper;

    private EcoNews ecoNews = ModelUtils.getEcoNews();

    @Test
    public void convertTest() {
        AddEcoNewsDtoRequest request = ModelUtils.getAddEcoNewsDtoRequest();

        EcoNews actual = mapper.convert(request);
        actual.setId(1L);
        actual.setCreationDate(ecoNews.getCreationDate());
        actual.setTags(Collections.singletonList(ModelUtils.getTag()));

        Assert.assertEquals(ecoNews, actual);
    }
}
