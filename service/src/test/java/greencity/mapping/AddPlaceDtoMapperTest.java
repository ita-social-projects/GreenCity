package greencity.mapping;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import greencity.ModelUtils;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.place.AddPlaceDto;
import greencity.dto.place.PlaceResponse;

@ExtendWith(MockitoExtension.class)
class AddPlaceDtoMapperTest {
    @InjectMocks
    AddPlaceDtoMapper mapper;

    @Test
    void convert() {
        AddPlaceDto addPlaceDto = ModelUtils.getAddPlaceDto();
        PlaceResponse placeResponse = PlaceResponse.builder()
            .openingHoursList(Set.of(OpeningHoursDto.builder()
                .weekDay(DayOfWeek.MONDAY)
                .openTime(LocalTime.now())
                .closeTime(LocalTime.now())
                .build()))
            .placeName("test")
            .build();

        Assertions.assertEquals(placeResponse, mapper.convert(addPlaceDto));
    }
}
