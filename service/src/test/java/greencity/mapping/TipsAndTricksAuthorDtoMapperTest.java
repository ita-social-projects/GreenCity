package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.TipsAndTricksAuthorDto;
import greencity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TipsAndTricksAuthorDtoMapperTest {

    @InjectMocks
    private TipsAndTricksAuthorDtoMapper tipsAndTricksAuthorDtoMapper;

    @Test
    void convert() {
        User author = ModelUtils.getUser();

        TipsAndTricksAuthorDto expected = TipsAndTricksAuthorDto.builder()
            .id(author.getId())
            .name(author.getName())
            .build();

        TipsAndTricksAuthorDto actual = tipsAndTricksAuthorDtoMapper.convert(author);

        assertEquals(expected, actual);

    }
}
