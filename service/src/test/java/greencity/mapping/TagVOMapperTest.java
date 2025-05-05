package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.language.LanguageDTO;
import greencity.dto.tag.TagTranslationVO;
import greencity.dto.tag.TagVO;
import greencity.entity.Tag;
import greencity.service.LanguageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagVOMapperTest {

    @Mock
    LanguageService languageService;

    @InjectMocks
    TagVOMapper mapper;

    @Test
    void convert() {
        Tag tag = ModelUtils.getTag();
        LanguageDTO language = ModelUtils.getLanguageDTO();

        TagVO expected = TagVO.builder()
            .id(tag.getId())
            .type(tag.getType())
            .tagTranslations(tag.getTagTranslations().stream()
                .map(tagTranslation -> {

                    when(languageService.findByCode(tagTranslation.getLanguageCode()))
                        .thenReturn(language);

                    return TagTranslationVO.builder()
                        .id(tagTranslation.getId())
                        .name(tagTranslation.getName())
                        .languageVO(LanguageDTO.builder()
                            .id(language.getId())
                            .code(language.getCode())
                            .build())
                        .build();
                })
                .collect(Collectors.toList()))
            .build();
        TagVO actual = mapper.convert(tag);

        assertEquals(expected, actual);
    }
}