package greencity.mapping;

import greencity.dto.place.PlaceVO;
import greencity.entity.Category;
import greencity.entity.Location;
import greencity.entity.Place;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class PlaceMapper extends AbstractConverter<PlaceVO, Place> {
    /**
     * Method convert {@link PlaceVO} to {@link Place}.
     *
     * @return {@link Place}
     */
    @Override
    protected Place convert(PlaceVO source) {
        Category category = Category.builder()
                .id(source.getCategory().getId())
                .name(source.getCategory().getName())
                .build();
        User user = User.builder()
                .id(source.getAuthor().getId())
                .build();
        Location localion = Location.builder()
                .id(source.getLocation().getId())
                .address(source.getLocation().getAddress())
                .lat(source.getLocation().getLat())
                .lng(source.getLocation().getLng())
                .build();
        return Place.builder()
                .id(source.getId())
                .description(source.getDescription())
                .email(source.getEmail())
                .modifiedDate(source.getModifiedDate())
                .name(source.getName())
                .phone(source.getPhone())
                .author(user)
                .category(category)
                .location(localion)
                .build();
    }
}
