package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.language.LanguageVO;
import greencity.dto.tag.TagTranslationVO;
import greencity.dto.tag.TagVO;
import greencity.entity.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TagVOMapperTest {

    @InjectMocks
    private TagVOMapper mapper;

    @Test
    void convert() {
        Tag tag = ModelUtils.getTag();

        TagVO expected = TagVO.builder()
            .id(tag.getId())
            .type(tag.getType())
            .tagTranslations(tag.getTagTranslations().stream()
                .map(tagTranslation -> TagTranslationVO.builder()
                    .id(tagTranslation.getId())
                    .name(tagTranslation.getName())
                    .languageVO(LanguageVO.builder()
                        .id(tagTranslation.getLanguage().getId())
                        .code(tagTranslation.getLanguage().getCode())
                        .build())
                    .build())
                .collect(Collectors.toList()))
            .build();
        TagVO actual = mapper.convert(tag);

        assertEquals(expected, actual);
    }
}