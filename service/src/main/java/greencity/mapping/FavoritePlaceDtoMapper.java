package greencity.mapping;

import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.entity.FavoritePlace;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class FavoritePlaceDtoMapper extends AbstractConverter<FavoritePlace, FavoritePlaceDto> {
    @Override
    public FavoritePlaceDto convert(FavoritePlace entity) {
        return FavoritePlaceDto.builder()
            .placeId(entity.getId())
            .name(entity.getName())
            .build();
    }
}
