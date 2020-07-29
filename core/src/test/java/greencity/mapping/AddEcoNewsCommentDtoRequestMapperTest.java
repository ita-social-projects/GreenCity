package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoRequest;
import greencity.entity.EcoNewsComment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static greencity.ModelUtils.getEcoNewsComment;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class AddEcoNewsCommentDtoRequestMapperTest {
    @InjectMocks
    private AddEcoNewsCommentDtoRequestMapper mapper;

    private final EcoNewsComment expected = getEcoNewsComment();

    @Test
    public void convertTest(){
        AddEcoNewsCommentDtoRequest addEcoNewsCommentDtoRequest = ModelUtils.getAddEcoNewsCommentDtoRequest();
        EcoNewsComment actual = mapper.convert(addEcoNewsCommentDtoRequest);
        assertEquals(expected.getText(), actual.getText());
    }
}