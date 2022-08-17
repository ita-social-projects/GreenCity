package greencity.mapping;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.place.PlaceResponse;
import greencity.entity.Place;

@ExtendWith(MockitoExtension.class)
class PlaceMapperTest {
    @InjectMocks
    PlaceMapper mapper;

    @Test
    void convertTest() {
        PlaceResponse placeResponse = PlaceResponse.builder()
            .placeName("test")
            .openingHoursList(Set.of(OpeningHoursDto.builder()
                .closeTime(LocalTime.now())
                .openTime(LocalTime.now())
                .weekDay(DayOfWeek.MONDAY)
                .build()))
            .build();
        Place place = Place.builder()
            .name("test")
            .build();

        Assertions.assertEquals(place, mapper.convert(placeResponse));
    }
}
