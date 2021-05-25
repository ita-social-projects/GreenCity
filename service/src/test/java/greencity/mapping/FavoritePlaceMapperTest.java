package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.entity.FavoritePlace;
import greencity.entity.Place;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class FavoritePlaceMapperTest {
    @InjectMocks
    private FavoritePlaceMapper favoritePlaceMapper;

    @Test
    void convertToEntityTest() {
        FavoritePlaceDto favoritePlaceDto = ModelUtils.getFavoritePlaceDto();
        Place place = Place.builder().id(favoritePlaceDto.getPlaceId()).build();

        FavoritePlace expected =
            new FavoritePlace(null, favoritePlaceDto.getName(), null, place);

        assertEquals(expected, favoritePlaceMapper.convert(favoritePlaceDto));
    }
}
