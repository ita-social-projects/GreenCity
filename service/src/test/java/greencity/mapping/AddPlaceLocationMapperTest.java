package greencity.mapping;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import greencity.ModelUtils;
import greencity.dto.location.AddPlaceLocation;
import greencity.entity.Location;

@ExtendWith(MockitoExtension.class)
class AddPlaceLocationMapperTest {
    @InjectMocks
    AddPlaceLocationMapper mapper;

    @Test
    void convert() {
        Location location = ModelUtils.getLocation();
        AddPlaceLocation addPlaceLocation = ModelUtils.getAddPlaceLocation();

        Assertions.assertEquals(location, mapper.convert(addPlaceLocation));
    }
}
