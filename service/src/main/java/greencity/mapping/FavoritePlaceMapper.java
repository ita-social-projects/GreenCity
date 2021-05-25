package greencity.mapping;

import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.entity.FavoritePlace;
import greencity.entity.Place;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class FavoritePlaceMapper extends AbstractConverter<FavoritePlaceDto, FavoritePlace> {
    @Override
    public FavoritePlace convert(FavoritePlaceDto dto) {
        return FavoritePlace.builder()
            .name(dto.getName())
            .place(Place.builder().id(dto.getPlaceId()).build())
            .build();
    }
}
