package greencity.mapping;

import greencity.dto.tag.TagDto;
import greencity.entity.Tag;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TagDtoMapper extends AbstractConverter<List<Tag>, List<TagDto>> {

    @Override
    protected List<TagDto> convert(List<Tag> tags) {
        List<TagDto> tagDtos = new ArrayList<>();
        for (Tag tag : tags) {
            tagDtos.add(new TagDto(tag.getName()));
        }
        return tagDtos;
    }
}
