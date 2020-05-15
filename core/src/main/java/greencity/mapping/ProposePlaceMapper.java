package greencity.mapping;

import greencity.dto.place.PlaceAddDto;
import greencity.entity.*;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

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
        place.setLocation(Location.builder()
                .address(placeAddDto.getLocation().getAddress())
                .lat(placeAddDto.getLocation().getLat())
                .lng(placeAddDto.getLocation().getLng())
                .build());
        place.setCategory(Category.builder()
                .name(placeAddDto.getCategory().getName())
                .build());
        place.setOpeningHoursList(placeAddDto.getOpeningHoursList().stream()
                .map(openingHoursDto -> {
                    OpeningHours openingHours = new OpeningHours();
                    openingHours.setOpenTime(openingHoursDto.getOpenTime());
                    openingHours.setCloseTime(openingHoursDto.getCloseTime());
                    openingHours.setBreakTime(BreakTime.builder()
                            .startTime(openingHoursDto.getBreakTime().getStartTime())
                            .endTime(openingHoursDto.getBreakTime().getEndTime())
                            .build());
                    openingHours.setWeekDay(openingHoursDto.getWeekDay());
                    return openingHours;
                }).collect(Collectors.toSet()));
        place.setDiscountValues(placeAddDto.getDiscountValues().stream()
                .map(discountValueDto -> {
                    DiscountValue discountValue = new DiscountValue();
                    discountValue.setValue(discountValueDto.getValue());
                    discountValue.setSpecification(Specification.builder()
                            .name(discountValueDto.getSpecification().getName())
                            .build());
                    return discountValue;
                }).collect(Collectors.toSet()));
        place.setPhotos(placeAddDto.getPhotos().stream()
                .map(photoAddDto -> {
                    Photo photo = new Photo();
                    photo.setName(photoAddDto.getName());
                    return photo;
                }).collect(Collectors.toList()));
        place.getOpeningHoursList().forEach(h -> h.setPlace(place));
        place.getPhotos().forEach(photo -> photo.setUser(place.getAuthor()));

        return place;
    }
}
