package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econews.EcoNewsGroupedTagsDto;
import greencity.entity.EcoNews;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
class EcoNewsGroupedTagsDtoMapperTest {
    @InjectMocks
    EcoNewsGroupedTagsDtoMapper mapper;

    @Test
    void convertTest() {
        EcoNews ecoNews = ModelUtils.getEcoNews();
        EcoNewsGroupedTagsDto expected = ModelUtils.getEcoNewsGroupedTagsDto();

        assertEquals(expected, mapper.convert(ecoNews));
    }

    @Test
    void convertNullTest() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {mapper.convert((EcoNews) null);});

        assertEquals("EcoNews cannot be null", exception.getMessage());
    }
}
