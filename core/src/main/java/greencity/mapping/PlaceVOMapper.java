package greencity.mapping;

import greencity.dto.place.PlaceVO;
import greencity.entity.Place;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link Place} into
 * {@link PlaceVO}.
 *
 * @author Vasyl Zhovnir
 */
@Component
public class PlaceVOMapper extends AbstractConverter<Place, PlaceVO> {
    /**
     * Method convert {@link Place} to {@link PlaceVO}.
     *
     * @return {@link PlaceVO}
     */
    @Override
    protected PlaceVO convert(Place source) {
        return PlaceVO.builder()
            .id(source.getId())
            .description(source.getDescription())
            .email(source.getEmail())
            .modifiedDate(source.getModifiedDate())
            .name(source.getName())
            .phone(source.getPhone())
            .authorId(source.getAuthor().getId())
            .categoryId(source.getCategory().getId())
            .locationId(source.getLocation().getId())
            .build();
    }
}
