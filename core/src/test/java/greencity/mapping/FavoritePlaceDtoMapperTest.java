package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.entity.FavoritePlace;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class FavoritePlaceDtoMapperTest {
    @InjectMocks
    private FavoritePlaceDtoMapper favoritePlaceDtoMapper;

    @Test
    public void convertToDtoTest() {
        FavoritePlace favoritePlace = ModelUtils.getFavoritePlace();

        FavoritePlaceDto expected = new FavoritePlaceDto(favoritePlace.getName(), favoritePlace.getId());

        assertEquals(expected, favoritePlaceDtoMapper.convert(favoritePlace));
    }
}

