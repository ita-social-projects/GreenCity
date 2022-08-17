package greencity.mapping;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import greencity.ModelUtils;
import greencity.dto.place.PlaceResponse;
import greencity.entity.Category;
import greencity.entity.Location;
import greencity.entity.OpeningHours;
import greencity.entity.Place;
import greencity.enums.PlaceStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PlaceResponseMapperTest {
    @InjectMocks
    private PlaceResponseMapper mapper;

    @Test
    void convert() {
        Place sourse = Place.builder()
            .status(PlaceStatus.APPROVED)
            .openingHoursList(Set.of(OpeningHours.builder()
                .weekDay(DayOfWeek.MONDAY)
                .closeTime(LocalTime.now())
                .openTime(LocalTime.now())
                .build()))
            .location(Location.builder()
                .lat(32.3)
                .lng(32.2)
                .address("test")
                .addressUa("test")
                .build())
            .category(Category.builder()
                .nameUa("Test")
                .name("category")
                .build())
            .build();

        PlaceResponse expected = ModelUtils.getPlaceResponse();

        assertEquals(expected, mapper.convert(sourse));
    }
}
