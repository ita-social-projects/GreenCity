package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.location.LocationDto;
import greencity.dto.place.PlaceByBoundsDto;
import greencity.entity.FavoritePlace;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;

import static org.junit.Assert.assertEquals;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class FavoritePlaceWithLocationMapperTest {
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private FavoritePlaceWithLocationMapper favoritePlaceWithLocationMapper;

    @Test
    public void convertTest() {
        FavoritePlace favoritePlace = ModelUtils.getFavoritePlace();
        PlaceByBoundsDto expected = new PlaceByBoundsDto(favoritePlace.getPlace().getId(), favoritePlace.getName(),
            modelMapper.map(favoritePlace.getPlace().getLocation(), LocationDto.class));

        assertEquals(expected, favoritePlaceWithLocationMapper.convertToDto(favoritePlace));
    }
}
