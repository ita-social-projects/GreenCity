package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.entity.FavoritePlace;
import greencity.entity.Place;
import greencity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import static org.junit.Assert.assertEquals;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class FavoritePlaceDtoMapperTest {
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

        FavoritePlaceDto expected = new FavoritePlaceDto(favoritePlace.getName(), favoritePlace.getId());

        assertEquals(expected, favoritePlaceDtoMapper.convertToDto(favoritePlace));
    }
}
