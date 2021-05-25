package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddTipsAndTricksCommentDtoResponseMapperTest {
    private AddTipsAndTricksCommentDtoResponseMapper mapper = new AddTipsAndTricksCommentDtoResponseMapper();

    @Test
    void convertTest() {
        AddTipsAndTricksCommentDtoResponse expected = ModelUtils.getAddTipsAndTricksCommentDtoResponse();
        AddTipsAndTricksCommentDtoResponse actual = mapper.convert(ModelUtils.getTipsAndTricksComment());
        assertEquals(expected, actual);
    }
}
