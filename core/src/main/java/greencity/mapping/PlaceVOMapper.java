package greencity.mapping;

import greencity.dto.category.CategoryVO;
import greencity.dto.location.LocationVO;
import greencity.dto.place.PlaceVO;
import greencity.dto.user.UserVO;
import greencity.entity.Location;
import greencity.entity.Place;
import greencity.entity.User;
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
        CategoryVO categoryVO = CategoryVO.builder()
            .id(source.getCategory().getId())
            .name(source.getCategory().getName())
            .build();
        UserVO userVO = UserVO.builder()
                .id(source.getAuthor().getId())
                .build();
        LocationVO localionVO = LocationVO.builder()
                .id(source.getLocation().getId())
                .address(source.getLocation().getAddress())
                .lat(source.getLocation().getLat())
                .lng(source.getLocation().getLng())
                .build();
        return PlaceVO.builder()
            .id(source.getId())
            .description(source.getDescription())
            .email(source.getEmail())
            .modifiedDate(source.getModifiedDate())
            .name(source.getName())
            .phone(source.getPhone())
            .author(userVO)
            .category(categoryVO)
            .location(localionVO)
            .build();
    }
}
