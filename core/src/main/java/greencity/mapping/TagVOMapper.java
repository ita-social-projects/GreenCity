package greencity.mapping;

import greencity.dto.tag.TagVO;
import greencity.entity.Tag;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link Tag} into
 * {@link TagVO}.
 *
 * @author Olena Petryshak
 */
@Component
public class TagVOMapper extends AbstractConverter<Tag, TagVO> {
    /**
     * Method convert {@link Tag} to {@link TagVO}.
     *
     * @return {@link TagVO}
     */
    @Override
    protected TagVO convert(Tag source) {
        return TagVO.builder()
            .id(source.getId())
            .name(source.getName())
            .build();
    }
}
