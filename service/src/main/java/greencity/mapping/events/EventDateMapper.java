package greencity.mapping.events;

import greencity.dto.event.EventDateDto;
import greencity.entity.EventDate;
import org.modelmapper.AbstractConverter;

public class EventDateMapper extends AbstractConverter<EventDateDto, EventDate> {
    @Override
    protected EventDate convert(EventDateDto source) {
        return EventDate.builder().startDate(source.getStartDate()).finishDate(source.getFinishDate())
            .build();
    }
}
