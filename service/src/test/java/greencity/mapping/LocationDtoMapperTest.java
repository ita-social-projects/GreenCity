package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.location.LocationDto;
import greencity.entity.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LocationDtoMapperTest {
    @InjectMocks
    private LocationDtoMapper mapper;

    @Test
    void convertTest() {
        Location location = ModelUtils.getLocation();

        LocationDto expected = LocationDto.builder()
            .id(location.getId())
            .lat(location.getLat())
            .lng(location.getLng())
            .address(location.getAddressEn())
            .build();

        LocationDto actual = mapper.convert(location);

        assertEquals(expected, actual);
    }

    @Test
    void convertNullTest() {
        assertNull(mapper.convert((Location) null));
    }

    @Test
    void convertEmptyObjectTest() {
        Location emptyLocation = new Location();

        LocationDto result = mapper.convert(emptyLocation);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getLat());
        assertNull(result.getLng());
        assertNull(result.getAddress());
    }
}
