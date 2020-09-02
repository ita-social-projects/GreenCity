package greencity.mapping;

import greencity.dto.place.PlaceAddDto;
import greencity.entity.*;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


/**
 * Class that used by {@link ModelMapper} to map {@link PlaceAddDto} into
 * {@link Place}.
 *
 * @author Marian Datsko
 */
@Component
public class ProposePlaceMapper extends AbstractConverter<PlaceAddDto, Place> {
    /**
     * Method convert {@link PlaceAddDto} to {@link Place}.
     *
     * @return {@link Place}
     */
    @Override
    protected Place convert(PlaceAddDto placeAddDto) {
        Place place = new Place();
        place.setName(placeAddDto.getName());
        place.setLocation(UtilsMapper.map(placeAddDto.getLocation(), Location.class));
        place.setCategory(UtilsMapper.map(placeAddDto.getCategory(), Category.class));
        place.setOpeningHoursList(UtilsMapper.mapAllToSet(placeAddDto.getOpeningHoursList(), OpeningHours.class));
        place.setDiscountValues(UtilsMapper.mapAllToSet(placeAddDto.getDiscountValues(), DiscountValue.class));
        place.setPhotos(UtilsMapper.mapAllToList(placeAddDto.getPhotos(), Photo.class));

        place.getOpeningHoursList().forEach(h -> h.setPlace(place));
        place.getPhotos().forEach(photo -> photo.setUser(place.getAuthor()));

        return place;
    }
}
