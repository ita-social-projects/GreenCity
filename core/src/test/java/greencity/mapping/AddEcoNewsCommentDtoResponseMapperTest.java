package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoResponse;
import greencity.entity.EcoNewsComment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static greencity.ModelUtils.getEcoNewsComment;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class AddEcoNewsCommentDtoResponseMapperTest {
    @InjectMocks
    private AddEcoNewsCommentDtoResponseMapper mapper;

    private final EcoNewsComment ecoNewsComment = getEcoNewsComment();

    @Test
    public void convertTest() {
        AddEcoNewsCommentDtoResponse expected = ModelUtils.getAddEcoNewsCommentDtoResponse();
        AddEcoNewsCommentDtoResponse actual = mapper.convert(ecoNewsComment);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getText(), actual.getText());
    }
}