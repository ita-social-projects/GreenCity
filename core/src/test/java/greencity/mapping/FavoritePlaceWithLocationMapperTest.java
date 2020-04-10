package greencity.mapping;


import greencity.ModelUtils;
import greencity.dto.location.LocationDto;
import greencity.dto.place.PlaceByBoundsDto;
import greencity.entity.FavoritePlace;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
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
