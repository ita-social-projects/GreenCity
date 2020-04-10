package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.entity.FavoritePlace;
import greencity.entity.Place;
import greencity.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class FavoritePlaceDtoMapperTest {
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private FavoritePlaceDtoMapper favoritePlaceDtoMapper;

    @Test
    public void convertToEntityTest() {
        FavoritePlaceDto favoritePlaceDto = ModelUtils.getFavoritePlaceDto();
        Place place = Place.builder().id(favoritePlaceDto.getPlaceId()).build();
        User user = User.builder().id(favoritePlaceDto.getPlaceId()).build();

        FavoritePlace expected =
            new FavoritePlace(favoritePlaceDto.getPlaceId(), favoritePlaceDto.getName(), user, place);

        assertEquals(expected, favoritePlaceDtoMapper.convertToEntity(favoritePlaceDto));
    }

    @Test
    public void convertToDtoTest() {
        FavoritePlace favoritePlace = ModelUtils.getFavoritePlace();

        FavoritePlaceDto expected = new FavoritePlaceDto(favoritePlace.getName(), favoritePlace.getPlace().getId());

        assertEquals(expected, favoritePlaceDtoMapper.convertToDto(favoritePlace));
    }
}
