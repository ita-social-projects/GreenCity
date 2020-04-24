package greencity.mapping;

import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.entity.FavoritePlace;
import greencity.entity.Place;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class FavoritePlaceMapper extends AbstractConverter<FavoritePlaceDto, FavoritePlace> {
    @Override
    public FavoritePlace convert(FavoritePlaceDto dto) {
        return FavoritePlace.builder()
            .id(dto.getPlaceId())
            .name(dto.getName())
            .user(User.builder().id(dto.getPlaceId()).build())
            .place(Place.builder().id(dto.getPlaceId()).build())
            .build();
    }
}
