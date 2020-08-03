package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddTipsAndTricksCommentDtoResponseMapperTest {
    private AddTipsAndTricksCommentDtoResponseMapper mapper = new AddTipsAndTricksCommentDtoResponseMapper();

    @Test
    public void convertTest(){
        AddTipsAndTricksCommentDtoResponse expected = ModelUtils.getAddTipsAndTricksCommentDtoResponse();
        AddTipsAndTricksCommentDtoResponse actual = mapper.convert(ModelUtils.getTipsAndTricksComment());
        assertEquals(expected, actual);
    }
}
