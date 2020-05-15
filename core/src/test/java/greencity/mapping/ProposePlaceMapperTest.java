package greencity.mapping;

import greencity.dto.breaktime.BreakTimeDto;
import greencity.dto.category.CategoryDto;
import greencity.dto.discount.DiscountValueDto;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.photo.PhotoAddDto;
import greencity.dto.place.PlaceAddDto;
import greencity.dto.specification.SpecificationNameDto;
import greencity.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class ProposePlaceMapperTest {

    @InjectMocks
    private ProposePlaceMapper proposePlaceMapper;

    @Test
    void convert() {
        OpeningHoursDto openingHours = new OpeningHoursDto();
        openingHours.setOpenTime(LocalTime.of(7, 20, 45, 342123342));
        openingHours.setCloseTime(LocalTime.of(7, 20, 45, 342123342));
        openingHours.setBreakTime(BreakTimeDto.builder()
                .startTime(LocalTime.of(7, 20, 45, 342123342))
                .endTime(LocalTime.of(7, 20, 45, 342123342))
                .build());
        openingHours.setWeekDay(DayOfWeek.MONDAY);

        CategoryDto category = CategoryDto.builder()
                .name("category")
                .build();

        PhotoAddDto photo = new PhotoAddDto();
        photo.setName("photo");

        DiscountValueDto discountValue = new DiscountValueDto();
        discountValue.setValue(11);
        discountValue.setSpecification(new SpecificationNameDto("specif"));

        LocationAddressAndGeoDto address = LocationAddressAndGeoDto.builder()
                .address("address")
                .lat(12.56d)
                .lng(12.56d)
                .build();

        PlaceAddDto placeAddDto = PlaceAddDto.builder()
                .category(category)
                .name("place")
                .photos(Collections.singletonList(photo))
                .discountValues(Collections.singleton(discountValue))
                .location(address)
                .openingHoursList(Collections.singleton(openingHours))
                .build();


        OpeningHours openingHours1 = new OpeningHours();
        openingHours1.setOpenTime(LocalTime.of(7, 20, 45, 342123342));
        openingHours1.setCloseTime(LocalTime.of(7, 20, 45, 342123342));
        openingHours1.setBreakTime(BreakTime.builder()
                .startTime(LocalTime.of(7, 20, 45, 342123342))
                .endTime(LocalTime.of(7, 20, 45, 342123342))
                .build());
        openingHours1.setWeekDay(DayOfWeek.MONDAY);

        DiscountValue discountValue1 = new DiscountValue();
        discountValue1.setValue(11);
        discountValue1.setSpecification(Specification.builder()
                .name("specif")
                .build());

        Photo photo1 = new Photo();
        photo1.setName("photo");

        Place place = new Place();
        place.setName("place");
        place.setLocation(Location.builder()
                .address("address")
                .lat(12.56d)
                .lng(12.56d)
                .build());
        place.setCategory(Category.builder()
                .name("category")
                .build());
        place.setOpeningHoursList(Collections.singleton(openingHours1));
        place.setDiscountValues(Collections.singleton(discountValue1));
        place.setPhotos(Collections.singletonList(photo1));
        place.getOpeningHoursList().forEach(h -> h.setPlace(place));
        place.getPhotos().forEach(photo11 -> photo11.setUser(place.getAuthor()));

        assertEquals(place, proposePlaceMapper.convert(placeAddDto));
    }
}