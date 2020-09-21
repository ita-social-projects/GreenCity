package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoResponse;
import greencity.entity.EcoNewsComment;
import org.junit.jupiter.api.Test;

import static greencity.ModelUtils.getEcoNewsComment;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AddEcoNewsCommentDtoResponseMapperTest {

    private AddEcoNewsCommentDtoResponseMapper mapper = new AddEcoNewsCommentDtoResponseMapper();

    private final EcoNewsComment ecoNewsComment = getEcoNewsComment();

    @Test
    void convertTest() {
        AddEcoNewsCommentDtoResponse expected = ModelUtils.getAddEcoNewsCommentDtoResponse();
        AddEcoNewsCommentDtoResponse actual = mapper.convert(ecoNewsComment);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getText(), actual.getText());
        assertEquals(expected.getAuthor(), actual.getAuthor());
    }
}