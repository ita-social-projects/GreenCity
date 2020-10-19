package greencity.mapping;

import greencity.dto.specification.SpecificationVO;
import greencity.dto.tag.TagVO;
import greencity.entity.Specification;
import greencity.entity.Tag;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link Specification} into
 * {@link SpecificationVO}.
 *
 * @author Olena Petryshak
 */
@Component
public class SpecificationVOMapper extends AbstractConverter<Specification, SpecificationVO> {
    /**
     * Method convert {@link Tag} to {@link TagVO}.
     *
     * @return {@link TagVO}
     */
    @Override
    protected SpecificationVO convert(Specification source) {
        return SpecificationVO.builder()
            .id(source.getId())
            .name(source.getName())
            .build();
    }
}
