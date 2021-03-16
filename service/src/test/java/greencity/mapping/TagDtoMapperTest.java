package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.tag.TagDto;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TagDtoMapperTest {

    @InjectMocks
    private TagDtoMapper mapper;

    @Test
    void convert() {
        TagTranslation tagTranslation = TagTranslation.builder()
            .id(1L)
            .tag(ModelUtils.getTag())
            .name("News")
            .build();

        TagDto expected = TagDto.builder()
            .id(tagTranslation.getTag().getId())
            .name(tagTranslation.getName())
            .build();

        TagDto actual = mapper.convert(tagTranslation);

        assertEquals(expected, actual);
    }
}