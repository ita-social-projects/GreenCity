package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.tag.TagVO;
import greencity.entity.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TagMapperTest {
    @InjectMocks
    private TagMapper mapper;

    @Test
    void convert() {
        TagVO tagVO = ModelUtils.getTagVO();
        Tag expected = ModelUtils.getTag();

        Tag actual = mapper.convert(tagVO);

        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getId(), actual.getId());
    }
}
