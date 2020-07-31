package greencity.mapping;

import greencity.ModelUtils;
import greencity.entity.TipsAndTricksComment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddTipsAndTricksCommentDtoRequestMapperTest {
    private AddTipsAndTricksCommentDtoRequestMapper mapper = new AddTipsAndTricksCommentDtoRequestMapper();

    @Test
    public void convertTest() {
        TipsAndTricksComment expected = ModelUtils.getTipsAndTricksComment();
        TipsAndTricksComment actual = mapper.convert(ModelUtils.getAddTipsAndTricksCommentDtoRequest());
        actual.setId(1L);
        assertEquals(expected, actual);
    }
}
