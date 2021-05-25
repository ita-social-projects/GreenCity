package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.entity.EcoNews;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class AddEcoNewsDtoResponseMapperTest {

    @InjectMocks
    private AddEcoNewsDtoResponseMapper mapper;

    @Test
    void convertTest() {
        EcoNews ecoNews = ModelUtils.getEcoNews();

        AddEcoNewsDtoResponse expected = ModelUtils.getAddEcoNewsDtoResponse();
        expected.setCreationDate(ecoNews.getCreationDate());

        assertEquals(expected, mapper.convert(ecoNews));
    }
}
