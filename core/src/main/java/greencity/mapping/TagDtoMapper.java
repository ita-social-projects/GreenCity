package greencity.mapping;

import greencity.dto.tag.TagDto;
import greencity.entity.Tag;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class TagDtoMapper extends AbstractConverter<Tag, TagDto> {
    @Override
    protected TagDto convert(Tag tag) {
        TagDto tagDto = new TagDto(tag.getName());
        return tagDto;
    }
}
