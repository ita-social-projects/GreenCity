package greencity.mapping.events;

import greencity.dto.event.CoordinatesDto;
import greencity.entity.event.Coordinates;
import org.modelmapper.AbstractConverter;

public class CoordinatesDtoMapper extends AbstractConverter<CoordinatesDto, Coordinates> {
    @Override
    protected Coordinates convert(CoordinatesDto coordinatesDto) {
        return Coordinates.builder().latitude(coordinatesDto.getLatitude())
            .longitude(coordinatesDto.getLongitude())
            .streetEn(coordinatesDto.getStreetEn())
            .streetUa(coordinatesDto.getStreetUa())
            .houseNumber(coordinatesDto.getHouseNumber())
            .cityEn(coordinatesDto.getCityEn())
            .cityUa(coordinatesDto.getCityUa())
            .regionEn(coordinatesDto.getRegionEn())
            .regionUa(coordinatesDto.getRegionUa())
            .countryEn(coordinatesDto.getCountryEn())
            .countryUa(coordinatesDto.getCountryUa()).build();
    }
}
