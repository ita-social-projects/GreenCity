package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoRequest;
import greencity.entity.TipsAndTricksComment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddTipsAndTricksCommentDtoRequestMapperTest {
    private AddTipsAndTricksCommentDtoRequestMapper mapper = new AddTipsAndTricksCommentDtoRequestMapper();

    @Test
    void convertTest() {
        TipsAndTricksComment expected = TipsAndTricksComment.builder()
                .id(1L)
                .text("TipsAndTricksComment")
                .build();
        AddTipsAndTricksCommentDtoRequest addTipsAndTricksCommentDtoRequest = ModelUtils.getAddTipsAndTricksCommentDtoRequest();
        TipsAndTricksComment actual = mapper.convert(addTipsAndTricksCommentDtoRequest);
        actual.setId(1L);
        assertEquals(expected, actual);
    }
}
