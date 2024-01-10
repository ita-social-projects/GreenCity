package greencity.mapping;

import java.util.stream.Collectors;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;
import greencity.dto.place.PlaceResponse;
import greencity.entity.OpeningHours;
import greencity.entity.Place;
import greencity.enums.PlaceStatus;

@Component
public class PlaceMapper extends AbstractConverter<PlaceResponse, Place> {
    @Override
    protected Place convert(PlaceResponse source) {
        return Place.builder()
            .name(source.getPlaceName())
            .openingHoursList(source.getOpeningHoursList().stream().map(hour -> OpeningHours.builder()
                .openTime(hour.getOpenTime())
                .closeTime(hour.getCloseTime())
                .weekDay(hour.getWeekDay())
                .build())
                .collect(Collectors.toSet()))
            .status(PlaceStatus.APPROVED)
            .build();
    }
}
