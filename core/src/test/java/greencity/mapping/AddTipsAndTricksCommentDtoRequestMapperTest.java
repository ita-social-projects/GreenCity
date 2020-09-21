package greencity.mapping;

import greencity.ModelUtils;
import greencity.entity.TipsAndTricksComment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddTipsAndTricksCommentDtoRequestMapperTest {
    private AddTipsAndTricksCommentDtoRequestMapper mapper = new AddTipsAndTricksCommentDtoRequestMapper();

    @Test
    void convertTest() {
        TipsAndTricksComment expected = ModelUtils.getTipsAndTricksComment();
        TipsAndTricksComment actual = mapper.convert(ModelUtils.getAddTipsAndTricksCommentDtoRequest());
        actual.setId(1L);
        assertEquals(expected, actual);
    }
}
