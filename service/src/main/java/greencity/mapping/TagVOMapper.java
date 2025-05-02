package greencity.mapping;

import greencity.dto.language.LanguageVO;
import greencity.dto.tag.TagTranslationVO;
import greencity.dto.tag.TagVO;
import greencity.entity.Tag;
import greencity.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TagVOMapper extends AbstractConverter<Tag, TagVO> {
    private final LanguageService languageService;

    @Override
    protected TagVO convert(Tag tag) {
        return TagVO.builder()
            .id(tag.getId())
            .type(tag.getType())
            .tagTranslations(tag.getTagTranslations().stream()
                .map(tagTranslation -> {
                    Long languageId = tagTranslation.getLanguageId();
                    LanguageVO languageVO = languageService.findLanguageById(languageId);

                    return TagTranslationVO.builder()
                            .id(tagTranslation.getId())
                            .name(tagTranslation.getName())
                            .languageVO(LanguageVO.builder()
                                    .id(languageVO.getId())
                                    .code(languageVO.getCode())
                                    .build())
                            .build();
                })
                .collect(Collectors.toList()))
            .build();
    }
}
