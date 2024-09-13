package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.location.LocationDto;
import greencity.dto.place.PlaceInfoDto;
import greencity.entity.Place;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

@ExtendWith(SpringExtension.class)
class PlaceInfoDtoMapperTest {
    @InjectMocks
    private PlaceInfoDtoMapper mapper;

    @Test
    void convert() {
        Place place = ModelUtils.getPlace();

        LocationDto locationDto = LocationDto.builder()
            .id(place.getLocation().getId())
            .lat(place.getLocation().getLat())
            .lng(place.getLocation().getLng())
            .address(place.getLocation().getAddress()).build();

        PlaceInfoDto placeInfoDto = new PlaceInfoDto();
        placeInfoDto.setId(1L);
        placeInfoDto.setName("Forum");
        placeInfoDto.setLocation(locationDto);
        placeInfoDto.setPlaceImages(new ArrayList<>());
        placeInfoDto.setDescription(place.getDescription());
        placeInfoDto.setWebsiteUrl(place.getEmail());
        placeInfoDto.setOpeningHoursList(new ArrayList<>());

        Assertions.assertEquals(placeInfoDto, mapper.convert(place));
    }
}
