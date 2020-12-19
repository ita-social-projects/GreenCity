package greencity.mapping;

import greencity.dto.tag.TagDto;
import greencity.entity.localization.TagTranslation;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class TagDtoMapper extends AbstractConverter<TagTranslation, TagDto> {
    @Override
    protected TagDto convert(TagTranslation tagTranslation) {
        return TagDto.builder()
            .id(tagTranslation.getTag().getId())
            .name(tagTranslation.getName())
            .build();
    }
}
