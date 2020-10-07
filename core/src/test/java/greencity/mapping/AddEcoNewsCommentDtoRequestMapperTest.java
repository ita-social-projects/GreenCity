package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoRequest;
import greencity.entity.EcoNewsComment;
import org.junit.jupiter.api.Test;

import static greencity.ModelUtils.getEcoNewsComment;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AddEcoNewsCommentDtoRequestMapperTest {
    private AddEcoNewsCommentDtoRequestMapper mapper = new AddEcoNewsCommentDtoRequestMapper();

    private final EcoNewsComment expected = getEcoNewsComment();

    @Test
    void convertTest() {
        AddEcoNewsCommentDtoRequest addEcoNewsCommentDtoRequest = ModelUtils.getAddEcoNewsCommentDtoRequest();
        EcoNewsComment actual = mapper.convert(addEcoNewsCommentDtoRequest);
        assertEquals(expected.getText(), actual.getText());
    }
}