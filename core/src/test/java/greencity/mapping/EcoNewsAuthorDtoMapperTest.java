package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.User;
import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class EcoNewsAuthorDtoMapperTest {
    @InjectMocks
    private EcoNewsAuthorDtoMapper ecoNewsAuthorDtoMapper;

    @Test
    public void convertTest() {
        User author = ModelUtils.getUser();

        EcoNewsAuthorDto expected = new EcoNewsAuthorDto(author.getId(), author.getName());

        assertEquals(expected, ecoNewsAuthorDtoMapper.convert(author));
    }
}
